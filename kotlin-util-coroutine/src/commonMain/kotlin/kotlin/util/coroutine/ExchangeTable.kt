package de.quati.kotlin.util.coroutine

import de.quati.kotlin.util.QuatiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onSubscription
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A concurrency utility for coordinating request/response style exchanges between coroutines.
 *
 * The table maintains two sides of communication:
 * - **Send table**: holds values that represent requests ([VS]).
 * - **Receive table**: holds values that represent responses ([VR]).
 *
 * A request is initiated by calling [waitFor], which stores the request data and suspends
 * until a corresponding response is provided by [notify]. After completion (either by success
 * or timeout), both request and response entries are cleaned up automatically.
 *
 * ### Key Properties
 * - Identifiers are composed of two parts: [ID0] (outer) and [ID1] (inner).
 * - Each `(id0, id1)` pair may have at most **one active waiter** at a time.
 * - Requests and responses are timestamped internally to support cleanup.
 *
 * This abstraction is useful for modeling one-off message exchanges, such as
 * RPC-style request/response patterns or matching commands with replies.
 *
 * @param ID0 type of the outer identifier
 * @param ID1 type of the inner identifier
 * @param VS type of values stored in the send table (requests)
 * @param VR type of values stored in the receive table (responses)
 * @constructor optionally provide a [Clock] (defaults to [Clock.System]) for timestamping
 */
public class ExchangeTable<ID0 : Any, ID1 : Any, VS : Any, VR : Any>
@OptIn(ExperimentalTime::class) constructor(clock: Clock) {

    public constructor() : this(@OptIn(ExperimentalTime::class) Clock.System)

    @OptIn(ExperimentalTime::class)
    private val flowsReceive = StateFlowTable<ID0, ID1, VR>(clock)

    @OptIn(ExperimentalTime::class)
    private val flowsSend = StateFlowTable<ID0, ID1, VS>(clock)

    /**
     * Drops all entries (both send and receive) whose timestamps are older than [timestamp].
     *
     * This is useful for periodic cleanup of stale requests or responses that
     * were never completed.
     *
     * @param timestamp all entries strictly before this instant will be removed
     */
    @OptIn(ExperimentalTime::class)
    public suspend fun dropAllBefore(timestamp: Instant) {
        flowsReceive.dropAllBefore(timestamp)
        flowsSend.dropAllBefore(timestamp)
    }

    /**
     * Waits for a response associated with the given [id0] and [id1].
     *
     * This call establishes a one-time request/response exchange:
     * - When subscribed, the provided [data] is published into the send table.
     * - The function then suspends until a matching response is delivered
     *   via [notify] for the same `(id0, id1)`.
     * - If a [timeout] is specified, the suspension will be cancelled after
     *   the given duration, and a [QuatiException.GatewayTimeout] is thrown.
     *
     * ### Exclusivity
     * Only **one active waiter** is allowed per `(id0, id1)` pair.
     * If another waiter already exists, this function fails immediately with
     * [QuatiException.InternalServerError].
     *
     * After either a response or timeout, both the request and response entries
     * are automatically removed from the exchange table.
     *
     * @param id0 the outer identifier
     * @param id1 the inner identifier
     * @param data the request payload to send
     * @param timeout optional maximum time to wait for a response
     * @return the response payload sent via [notify]
     * @throws QuatiException.InternalServerError if a waiter already exists
     * @throws QuatiException.GatewayTimeout if no response is received in time
     */
    public suspend fun waitFor(
        id0: ID0,
        id1: ID1,
        data: VS,
        timeout: Duration? = null,
    ): VR {
        val response = withNullableTimeoutOrNull(timeout) {
            flowsReceive.get(id0).onSubscription {
                flowsSend.update(id0 = id0, id1 = id1) { _, old ->
                    if (old != null)
                        throw QuatiException.InternalServerError("Data for id0=$id0, id1=$id1 already in exchange")
                    data
                }
            }.mapNotNull { it[id1] }
                .first()
        }
        flowsReceive.drop(id0, id1)
        flowsSend.drop(id0, id1)
        if (response == null) throw QuatiException.GatewayTimeout("Timeout waiting for data")
        return response.value
    }

    /**
     * Notifies the exchange with a response for the given [id0] and [id1].
     *
     * - The response [data] is stored in the receive table, making it available
     *   to a waiting [waitFor] call.
     * - The corresponding request entry in the send table is dropped.
     *
     * If no waiter is active for `(id0, id1)`, the response will still be written,
     * but is typically dropped during cleanup if never consumed.
     *
     * @param id0 the outer identifier
     * @param id1 the inner identifier
     * @param data the response payload
     */
    public suspend fun notify(id0: ID0, id1: ID1, data: VR) {
        flowsReceive.set(id0 = id0, id1 = id1, value = data)
        flowsSend.drop(id0 = id0, id1 = id1)
    }

    /**
     * Subscribes to request events in the send table for a given [id].
     *
     * The returned [Flow] emits [StateEvent] instances whenever a request
     * is created, updated, or deleted under the given [id].
     *
     * This is typically used by consumers who want to observe all outgoing
     * requests for a particular [id].
     *
     * @param id the outer identifier
     * @return a [Flow] of [StateEvent] describing request changes
     */
    public fun subscribeSend(id: ID0): Flow<StateEvent<ID1, VS>> = flowsSend.subscribe(id = id)
}

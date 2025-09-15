package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.WithTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

public sealed interface StateEvent<K : Any, V : Any> {
    public val key: K

    public data class Set<K : Any, V : Any>(
        public override val key: K,
        public val old: V?,
        public val new: V,
    ) : StateEvent<K, V>

    public data class Unset<K : Any, V : Any>(
        public override val key: K,
        public val old: V,
    ) : StateEvent<K, V>
}

/**
 * A two-level concurrent state table based on [MutableStateFlow].
 *
 * - The first level (ID0) groups entries into sub-tables.
 * - The second level (ID1) holds values of type [V] along with their timestamp ([WithTimestamp]).
 *
 * Each sub-table is a [MutableStateFlow] of a map, so updates can be observed reactively.
 *
 * @param ID0 the type of the outer key
 * @param ID1 the type of the inner key
 * @param V the type of the stored value
 * @property clock the clock used to assign timestamps to values
 */
public class StateFlowTable<ID0 : Any, ID1 : Any, V : Any> @OptIn(ExperimentalTime::class) constructor(
    private val clock: Clock,
) {
    /**
     * Default constructor that uses the system clock.
     */
    public constructor() : this(@OptIn(ExperimentalTime::class) Clock.System)

    private val data = ConcurrentQuatiMap<ID0, MutableStateFlow<Map<ID1, WithTimestamp<V>>>>()

    /**
     * Removes all entries (across all IDs) that were set before the given [timestamp].
     *
     * @param timestamp threshold timestamp; all values older than this are dropped
     */
    @OptIn(ExperimentalTime::class)
    public suspend fun dropAllBefore(timestamp: Instant) {
        data.getCurrentKeys().forEach { key ->
            getIntern(key).update { old ->
                val toDelete = old.mapNotNull { (k, v) -> if (v.timestamp < timestamp) k else null }
                if (toDelete.isEmpty()) return@update old
                val new = old.toMutableMap()
                toDelete.forEach { new.remove(it) }
                new.toMap()
            }
        }
    }

    /**
     * Removes a specific entry identified by [id0] and [id1].
     */
    public suspend fun drop(id0: ID0, id1: ID1): Unit = update(id0, id1) { _, _ -> null }

    /**
     * Sets a new [value] for the given [id0] and [id1].
     *
     * @param id0 the outer key
     * @param id1 the inner key
     * @param value the new value to store
     */
    public suspend fun set(id0: ID0, id1: ID1, value: V): Unit = update(id0, id1) { _, _ -> value }

    /**
     * Updates the entry for the given [id0] and [id1] using [block].
     *
     * - If [block] returns `null`, the entry is removed.
     * - If the new value is equal to the old one, no update is made.
     * - Otherwise, the new value is stored with an updated timestamp.
     *
     * @param id0 the outer key
     * @param id1 the inner key
     * @param block a function receiving the inner key and current value,
     *   returning the new value (or `null` to remove it)
     */
    @OptIn(ExperimentalTime::class)
    public suspend fun update(id0: ID0, id1: ID1, block: (ID1, V?) -> V?) {
        getIntern(id0).update { oldState ->
            val old = oldState[id1]?.value
            val new = block(id1, old)
            if (new == old)
                return@update oldState
            val newState = oldState.toMutableMap().also {
                if (new == null)
                    it.remove(id1)
                else
                    it[id1] = WithTimestamp(value = new, timestamp = clock.now())
            }.toMap()
            newState
        }
    }

    /**
     * Subscribes to changes for all entries under the given [id].
     *
     * Emits [StateEvent]s whenever a key is set, updated, or unset.
     *
     * @param id the outer key
     * @return a [Flow] of [StateEvent]s describing changes
     */
    public fun subscribe(id: ID0): Flow<StateEvent<ID1, V>> = flow {
        get(id).fold(initial = emptyMap<ID1, WithTimestamp<V>>()) { prev, state ->
            (prev.keys + state.keys).forEach { key -> // TODO, this is slow if maps are big
                val v0 = prev[key]?.value
                val v1 = state[key]?.value
                if (v1 == null)
                    emit(StateEvent.Unset(key = key, old = v0!!))
                else if (v1 != v0)
                    emit(StateEvent.Set(key = key, old = v0, new = v1))

            }
            state
        }
    }

    private suspend fun getIntern(id: ID0): MutableStateFlow<Map<ID1, WithTimestamp<V>>> =
        data.getOrPut(id) { MutableStateFlow(mapOf()) }

    /**
     * Gets the current state flow for the given [id].
     *
     * @param id the outer key
     * @return a read-only [StateFlow] of the map for that key
     */
    public suspend fun get(id: ID0): StateFlow<Map<ID1, WithTimestamp<V>>> = getIntern(id).asStateFlow()
}

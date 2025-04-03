package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
public fun <T, R> Flow<T>.mapParallel(
    concurrency: Int = DEFAULT_CONCURRENCY,
    block: suspend (T) -> R,
): Flow<R> = flatMapMerge(concurrency = concurrency) { flow { emit(block(it)) } }

public fun CoroutineScope.launchWorker(
    nofWorkers: Int,
    block: suspend CoroutineScope.(Int) -> Unit,
): List<Job> = (0..<nofWorkers).map { launch { block(it) } }

public fun <T> CoroutineScope.launchJobs(
    nofWorkers: Int,
    block: suspend CoroutineScope.(Int) -> T,
): List<Deferred<T>> = (0..<nofWorkers).map { async { block(it) } }

public suspend fun <R1, R2> awaitAll(
    f1: suspend CoroutineScope.() -> R1,
    f2: suspend CoroutineScope.() -> R2,
): Pair<R1, R2> = coroutineScope {
    val (r1, r2) = awaitAll(
        async(block = f1),
        async(block = f2)
    )
    @Suppress("UNCHECKED_CAST")
    (r1 as R1) to (r2 as R2)
}

public suspend fun <R1, R2, R3> awaitAll(
    f1: suspend CoroutineScope.() -> R1,
    f2: suspend CoroutineScope.() -> R2,
    f3: suspend CoroutineScope.() -> R3,
): Triple<R1, R2, R3> = coroutineScope {
    val (r1, r2, r3) = awaitAll(
        async(block = f1),
        async(block = f2),
        async(block = f3),
    )
    @Suppress("UNCHECKED_CAST")
    Triple(r1 as R1, r2 as R2, r3 as R3)
}

/**
 * A coroutine-based utility that coalesces multiple trigger calls into at most one active execution
 * of a suspending [task]. It runs the task in a dedicated coroutine and ensures that:
 *
 * - Only one instance of [task] runs at a time.
 * - If [schedule] is called while the task is running, the task is scheduled to run once more
 *   after the current execution finishes.
 * - Repeated calls to [schedule] while the task is running are coalesced into a single extra run.
 *
 * This is useful for scenarios like batching updates or refreshing state from external sources,
 * where overlapping invocations should not stack up but a final follow-up call should be ensured.
 *
 * Example:
 * ```
 * val runner = CoalescingTaskRunner(context = Dispatchers.Default) {
 *     println("Running update")
 *     delay(1000)
 * }
 *
 * repeat(10) { runner.schedule() } // Will run at most twice
 *
 * runner.close()
 * ```
 *
 * @param context The coroutine context used for the internal task runner.
 * @param errorHandler A callback invoked when [task] throws an exception.
 * @param task The suspending function to be executed with coalescing behavior.
 */
public class CoalescingTaskRunner(
    context: CoroutineContext = Dispatchers.Default,
    private val errorHandler: (Throwable) -> Unit = {},
    private val task: suspend () -> Unit,
) {
    private val scope: CoroutineScope = CoroutineScope(context + SupervisorJob())
    private val triggerChannel = Channel<Unit>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val runner = scope.launch {
        triggerChannel.consumeAsFlow().collect {
            try {
                task()
            } catch (t: Throwable) {
                runCatching { errorHandler(t) }
            }
        }
    }

    public fun schedule() {
        triggerChannel.trySend(Unit)
    }

    public suspend fun close() {
        triggerChannel.close()
        runner.join()
        scope.cancel()
    }

    public suspend fun <T> use(block: suspend (CoalescingTaskRunner) -> T): T = try {
        block(this)
    } finally {
        close()
    }
}
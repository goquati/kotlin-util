package de.quati.kotlin.util.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

public interface IConcurrentQuatiMap<K : Any, V : Any> {
    public suspend fun getCurrentKeys(): Set<K>
    public suspend fun getOrPut(key: K, initializer: () -> V): V
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect class ConcurrentQuatiMap<K : Any, V : Any>: IConcurrentQuatiMap<K,V>{
    public constructor()
    override suspend fun getCurrentKeys(): Set<K>
    override suspend fun getOrPut(key: K, initializer: () -> V): V
}

internal class ConcurrentQuatiMapSimple<K : Any, V : Any> : IConcurrentQuatiMap<K,V> {
    private val data = mutableMapOf<K, V>()
    private val mutex = Mutex()
    override suspend fun getCurrentKeys(): Set<K> = mutex.withLock { data.keys.toSet() }
    override suspend fun getOrPut(key: K, initializer: () -> V): V = mutex.withLock {
        data.getOrPut(key) { initializer() }
    }
}

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
 * @param context Coroutine context to run the task (defaults to [Dispatchers.Default]).
 * @param errorHandler Optional callback for handling exceptions thrown by the task.
 * @param task The suspending function to execute. Only one instance runs at a time.
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

/**
 * A coroutine-based utility that coalesces multiple invocations of a suspending task
 * and ensures only the latest scheduled invocation is executed, returning its result.
 *
 * This is useful in scenarios where a task may be triggered frequently (e.g., UI events,
 * network polling), but only the most recent execution and its result are relevant.
 *
 * Example use case: auto-refreshing data on user input where intermediate results can be discarded.
 *
 * The task is executed on a dedicated coroutine scope with optional error handling.
 * If a task is currently running and a new one is scheduled, the new task supersedes
 * previous pending triggers but waits for its result.
 *
 * @param context Coroutine context to run the task (defaults to [Dispatchers.Default]).
 * @param task The suspending function to execute. Only one instance runs at a time.
 * @param T The result type of the task.
 */
public class CoalescingTaskRunnerWithResult<T>(
    context: CoroutineContext = Dispatchers.Default,
    private val task: suspend () -> T,
) {
    private data class State<T>(
        val idRunning: Long?,
        val idLatest: Long,
        val value: T?,
    )

    private val result = MutableStateFlow<State<T>>(State(idRunning = null, idLatest = -1, value = null))
    private val scope: CoroutineScope = CoroutineScope(context + SupervisorJob())
    private val triggerChannel = Channel<Unit>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val runner = scope.launch {
        triggerChannel.consumeAsFlow().collect {
            val state = result.first()
            val idRunning = state.idLatest + 1
            result.value = state.copy(idRunning = idRunning)
            val value = task()
            result.value = State(idRunning = null, idLatest = idRunning, value = value)
        }
    }

    public suspend fun schedule(): T {
        val latestId = result.first().let { it.idRunning ?: it.idLatest }
        triggerChannel.send(Unit)
        return result.first { it.idLatest > latestId }.let {
            @Suppress("UNCHECKED_CAST")
            it.value as T
        }
    }

    public suspend fun close() {
        triggerChannel.close()
        runner.join()
        scope.cancel()
    }

    public suspend fun <R> use(block: suspend (CoalescingTaskRunnerWithResult<T>) -> R): R = try {
        block(this)
    } finally {
        close()
    }
}
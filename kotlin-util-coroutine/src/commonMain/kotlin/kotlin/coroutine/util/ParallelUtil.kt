package io.github.goquati.kotlin.coroutine.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
public fun <T, R> Flow<T>.mapParallel(
    concurrency: Int = DEFAULT_CONCURRENCY,
    block: suspend (T) -> R,
): Flow<R> = flatMapMerge(concurrency = concurrency) { flow { emit(block(it)) } }


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

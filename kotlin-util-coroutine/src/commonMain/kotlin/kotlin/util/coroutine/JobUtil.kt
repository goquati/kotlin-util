package de.quati.kotlin.util.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration


public fun CoroutineScope.loopCatching(
    handler: (Throwable) -> Unit,
    block: suspend CoroutineScope.() -> Unit,
): Job = launch {
    while (true) {
        try {
            block()
        } catch (t: Throwable) {
            handler(t)
        }
    }
}

public fun <T> CoroutineScope.lazyAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = async(
    context = context,
    start = CoroutineStart.LAZY,
    block = block,
)

public suspend fun <T> withNullableTimeout(
    timeout: Duration?,
    block: suspend () -> T,
): T = when (timeout) {
    null -> block()
    else -> withTimeout(timeout) { block() }
}

public suspend fun <T> withNullableTimeoutOrNull(
    timeout: Duration?,
    block: suspend () -> T,
): T? = when (timeout) {
    null -> block()
    else -> withTimeoutOrNull(timeout) { block() }
}

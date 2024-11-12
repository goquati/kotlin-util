package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


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

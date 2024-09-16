package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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
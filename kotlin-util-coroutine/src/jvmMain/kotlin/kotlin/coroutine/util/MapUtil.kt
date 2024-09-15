package io.github.goquati.kotlin.coroutine.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.coroutineContext


public suspend fun <K, V> ConcurrentMap<K, CompletableFuture<V>>.getOrPutAsync(
    key: K,
    block: suspend (K) -> V,
): V {
    val scope = CoroutineScope(coroutineContext)
    return computeIfAbsent(key) { scope.future { block(key) } }.await()
}

package io.github.goquati.kotlin.util.cache

import io.github.goquati.kotlin.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred

public interface ICache<K : Any, V : Any> {
    public suspend fun get(key: K, block: suspend CoroutineScope.(K) -> V): V
    public suspend fun getIfPresent(key: K): V?
    public suspend fun <E> getCatching(key: K, block: suspend CoroutineScope.(K) -> io.github.goquati.kotlin.util.Result<V, E>): Result<V, E>
    public fun put(key: K, value: V): V
    public suspend fun put(key: K, block: suspend CoroutineScope.(K) -> V): V
    public fun invalidate(key: K)
    public fun invalidateAll()
    public suspend fun asMap(): Map<K, V>
    public fun asDeferredMap(): Map<K, Deferred<V>>
}

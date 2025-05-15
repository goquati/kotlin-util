package io.github.goquati.kotlin.util.cache

import io.github.goquati.kotlin.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlin.coroutines.coroutineContext

public class CacheDummy<K : Any, V : Any>(
    private val defaultScope: CoroutineScope? = null,
) : ICache<K, V> {
    public constructor(dispatcher: CoroutineDispatcher)
            : this(CoroutineScope(dispatcher))

    private suspend fun getScope() = defaultScope ?: CoroutineScope(coroutineContext)

    public override suspend fun get(key: K, block: suspend CoroutineScope.(K) -> V): V =
        getScope().async { block(key) }.await()

    public override suspend fun getIfPresent(key: K): V? = null

    public override suspend fun <E> getCatching(
        key: K,
        block: suspend CoroutineScope.(K) -> Result<V, E>
    ): Result<V, E> = getScope().async { block(key) }.await()

    public override fun put(key: K, value: V): V = value
    public override suspend fun put(key: K, block: suspend CoroutineScope.(K) -> V): V =
        getScope().async { block(key) }.await()

    public override fun invalidate(key: K): Unit = Unit
    public override fun invalidateAll(): Unit = Unit
    public override suspend fun asMap(): Map<K, V> = emptyMap()
    public override fun asDeferredMap(): Map<K, Deferred<V>> = emptyMap()
}
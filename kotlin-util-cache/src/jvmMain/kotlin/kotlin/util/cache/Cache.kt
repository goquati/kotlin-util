package de.quati.kotlin.util.cache

import com.github.benmanes.caffeine.cache.*
import de.quati.kotlin.util.Failure
import de.quati.kotlin.util.Result
import de.quati.kotlin.util.Success
import de.quati.kotlin.util.getOr
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.await
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.toJavaDuration

public class Cache<K : Any, V : Any>(
    private val defaultScope: CoroutineScope?,
    internal val cache: AsyncCache<K, V>,
) : ICache<K, V> {
    private suspend fun getScope() = defaultScope ?: CoroutineScope(coroutineContext)

    public override suspend fun get(key: K, block: suspend CoroutineScope.(K) -> V): V {
        val scope = getScope()
        return cache.get(key) { k, _ -> scope.future { block(k) } }.await()
    }

    public override suspend fun getIfPresent(key: K): V? {
        return cache.getIfPresent(key)?.await()
    }

    public override suspend fun <E> getCatching(
        key: K,
        block: suspend CoroutineScope.(K) -> Result<V, E>,
    ): Result<V, E> {
        val scope = getScope()
        var error: E? = null
        val value = cache.get(key) { k, _ ->
            scope.future {
                block(k).getOr {
                    error = it
                    null
                }
            }
        }.await()
        error?.let { return Failure(it) }
        return Success(value!!)
    }

    public override fun put(key: K, value: V): V {
        cache.synchronous().put(key, value)
        return value
    }

    public override suspend fun put(
        key: K,
        block: suspend CoroutineScope.(K) -> V,
    ): V {
        return getScope().future { block(key) }
            .also { cache.put(key, it) }
            .await()
    }

    public override fun invalidate(key: K) {
        cache.synchronous().invalidate(key)
    }

    public override fun invalidateAll() {
        cache.synchronous().invalidateAll()
    }

    public override suspend fun asMap(): Map<K, V> {
        return cache.asMap().mapValues { it.value.await() }
    }

    public override fun asDeferredMap(): Map<K, Deferred<V>> {
        return cache.asMap().mapValues { it.value.asDeferred() }
    }

    public enum class RemovalCause(
        internal val caffeineCause: com.github.benmanes.caffeine.cache.RemovalCause,
    ) {
        EXPIRED(com.github.benmanes.caffeine.cache.RemovalCause.EXPIRED),
        REPLACED(com.github.benmanes.caffeine.cache.RemovalCause.REPLACED),
        COLLECTED(com.github.benmanes.caffeine.cache.RemovalCause.COLLECTED),
        EXPLICIT(com.github.benmanes.caffeine.cache.RemovalCause.EXPLICIT),
        SIZE(com.github.benmanes.caffeine.cache.RemovalCause.SIZE),
    }

    public class Builder<K : Any, V : Any> {
        private var initialCapacity: Int? = null
        private var ticker: (() -> Long)? = null
        private var evictionSizeBased: EvictionSizeBased<K, V>? = null
        private var evictionTimeBased: EvictionTimeBased<K, V>? = null
        private var removalListener: Listener<K, V>? = null
        private var defaultScope: CoroutineScope? = null

        public fun defaultScope(scope: CoroutineScope) {
            defaultScope = scope
        }

        public fun defaultDispatcher(dispatcher: CoroutineDispatcher) {
            defaultScope = CoroutineScope(dispatcher)
        }

        public fun capacity(size: Int) {
            initialCapacity = size
        }

        public fun ticker(block: () -> Long) {
            ticker = block
        }

        public fun removalListener(scope: CoroutineScope, block: suspend (K?, V?, RemovalCause) -> Unit) {
            removalListener = Listener(scope = scope, block = block)
        }

        public fun removalListener(dispatcher: CoroutineDispatcher, block: suspend (K?, V?, RemovalCause) -> Unit) {
            removalListener = Listener(
                scope = CoroutineScope(dispatcher + CoroutineName("Quati-Cache") + SupervisorJob()),
                block = block,
            )
        }

        public fun expirySizeBased(maxSize: Long) {
            evictionSizeBased = EvictionMaxSize(maxSize = maxSize)
        }

        public fun expirySizeBased(maxWeight: Long, weigher: (K, V) -> Int) {
            evictionSizeBased = EvictionMaxWeight(maxWeight = maxWeight, weigher = weigher)
        }

        public fun expiryTimeBased(
            afterWrite: Duration? = null,
            afterAccess: Duration? = null,
        ) {
            evictionTimeBased = EvictionTimeDuration(afterWrite = afterWrite, afterAccess = afterAccess)
        }

        public fun expiryTimeBased(
            afterWrite: (key: K, value: V, currentTime: Instant) -> Duration,
            afterRead: (key: K, value: V, currentTime: Instant, currentDuration: Duration) -> Duration,
        ) {
            evictionTimeBased = EvictionTimeFunction(afterWrite = afterWrite, afterRead = afterRead)
        }

        internal fun build(): Cache<K, V> {
            @Suppress("UNCHECKED_CAST")
            val caffeine = Caffeine.newBuilder() as Caffeine<K, V>
            when (val e = evictionTimeBased) {
                null -> Unit
                is EvictionTimeFunction -> caffeine.expireAfter(e)
                is EvictionTimeDuration -> {
                    e.afterAccess?.let { caffeine.expireAfterAccess(it.toJavaDuration()) }
                    e.afterWrite?.let { caffeine.expireAfterWrite(it.toJavaDuration()) }
                }
            }
            when (val e = evictionSizeBased) {
                null -> Unit
                is EvictionMaxSize -> caffeine.maximumSize(e.maxSize)
                is EvictionMaxWeight -> {
                    caffeine.maximumWeight(e.maxWeight)
                    caffeine.weigher(e.weigher)
                }
            }
            initialCapacity?.let { caffeine.initialCapacity(it) }
            removalListener?.let { caffeine.removalListener(it::notify) }
            ticker?.let { caffeine.ticker(it) }
            return Cache(
                defaultScope = defaultScope,
                cache = caffeine.buildAsync(),
            )
        }

        internal sealed interface EvictionSizeBased<K, V>

        internal data class EvictionMaxSize<K, V>(val maxSize: Long) : EvictionSizeBased<K, V>
        internal data class EvictionMaxWeight<K, V>(
            val maxWeight: Long,
            val weigher: (K, V) -> Int,
        ) : EvictionSizeBased<K, V>

        internal sealed interface EvictionTimeBased<K, V>

        internal data class EvictionTimeDuration<K, V>(
            val afterAccess: Duration?,
            val afterWrite: Duration?,
        ) : EvictionTimeBased<K, V>

        internal class EvictionTimeFunction<K, V>(
            private val afterWrite: (key: K, value: V, currentTime: Instant) -> Duration,
            private val afterRead: (key: K, value: V, currentTime: Instant, currentDuration: Duration) -> Duration,
        ) : EvictionTimeBased<K, V>, Expiry<K, V> {
            override fun expireAfterCreate(key: K, value: V, currentTime: Long): Long =
                afterWrite(key, value, currentTime.nanosToInstant()).inWholeNanoseconds

            override fun expireAfterUpdate(key: K, value: V, currentTime: Long, currentDuration: Long): Long =
                afterWrite(key, value, currentTime.nanosToInstant()).inWholeNanoseconds

            override fun expireAfterRead(key: K, value: V, currentTime: Long, currentDuration: Long): Long =
                afterRead(
                    key,
                    value,
                    currentTime.nanosToInstant(),
                    currentDuration.nanoseconds
                ).inWholeNanoseconds
        }

        private class Listener<K, V>(
            val scope: CoroutineScope,
            val block: suspend (K?, V?, RemovalCause) -> Unit,
        ) {
            fun notify(
                key: K?,
                value: V?,
                cause: com.github.benmanes.caffeine.cache.RemovalCause,
            ) = scope.launch {
                val quatiCause = RemovalCause.entries.first { it.caffeineCause == cause }
                block(key, value, quatiCause)
            }
        }
    }
}


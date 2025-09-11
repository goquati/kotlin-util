package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual class ConcurrentQuatiMap<K : Any, V : Any> : IConcurrentQuatiMap<K, V> {
    private val data = ConcurrentHashMap<K, V>()
    private val mutex = Mutex()
    actual override suspend fun getCurrentKeys(): Set<K> = mutex.withLock { data.keys.toSet() }
    actual override suspend fun getOrPut(key: K, initializer: () -> V): V = data[key] ?: mutex.withLock {
        data.getOrPut(key) { initializer() }
    }
}
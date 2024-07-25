package io.github.klahap.kotlin.util

public fun <T> Iterable<T>.take(n: Int, default: T): List<T> {
    val result = take(n)
    return result + List(n - result.size) { default }
}

public fun <T> Collection<T>.isDistinct(): Boolean =
    size == toSet().size

public inline fun <T, K> Iterable<T>.isDistinctBy(keySelector: (T) -> K): Boolean =
    map { keySelector(it) }.isDistinct()

public inline fun <T, K : Any> Iterable<T>.groupByNotNull(keySelector: (T) -> K?): Map<K, List<T>> =
    mapNotNull { (keySelector(it) ?: return@mapNotNull null) to it }
        .groupBy({ it.first }, { it.second })

public inline fun <T, K : Any, V : Any> Iterable<T>.groupByNotNull(
    keySelector: (T) -> K?,
    valueTransform: (T) -> V?
): Map<K, List<V>> =
    mapNotNull {
        val k = keySelector(it) ?: return@mapNotNull null
        val v = valueTransform(it) ?: return@mapNotNull null
        k to v
    }.groupBy({ it.first }, { it.second })


public inline fun <T, K : Any, V : Any> Iterable<T>.associateNotNull(
    valueSelector: (T) -> Pair<K?, V?>?,
): Map<K, V> = mapNotNull {
    val (k, v) = valueSelector(it) ?: return@mapNotNull null
    if (k == null || v == null) return@mapNotNull null
    k to v
}.toMap()

public inline fun <T, K : Any> Iterable<T>.associateByNotNull(
    keySelector: (T) -> K?,
): Map<K, T> = mapNotNull {
    (keySelector(it) ?: return@mapNotNull null) to it
}.toMap()

public inline fun <T, V : Any> Iterable<T>.associateWithNotNull(
    valueSelector: (T) -> V?,
): Map<T, V> = mapNotNull {
    it to (valueSelector(it) ?: return@mapNotNull null)
}.toMap()
package io.github.goquati.kotlin.util

public fun <T> Sequence<T>.take(n: Int, default: T): Sequence<T> = sequence {
    val result = take(n).toList()
    yieldAll(result)
    yieldAll(List(n - result.size) { default })
}

public fun <T> Sequence<T>.isDistinct(): Boolean =
    toList().isDistinct()

public inline fun <T, K> Sequence<T>.isDistinctBy(crossinline keySelector: (T) -> K): Boolean =
    map { keySelector(it) }.isDistinct()

public inline fun <T, K : Any> Sequence<T>.groupByNotNull(crossinline keySelector: (T) -> K?): Map<K, List<T>> =
    mapNotNull { (keySelector(it) ?: return@mapNotNull null) to it }
        .groupBy({ it.first }, { it.second })

public inline fun <T, K : Any, V : Any> Sequence<T>.groupByNotNull(
    crossinline keySelector: (T) -> K?,
    crossinline valueTransform: (T) -> V?
): Map<K, List<V>> =
    mapNotNull {
        val k = keySelector(it) ?: return@mapNotNull null
        val v = valueTransform(it) ?: return@mapNotNull null
        k to v
    }.groupBy({ it.first }, { it.second })

public inline fun <T, K : Any, V : Any> Sequence<T>.associateNotNull(
    crossinline valueSelector: (T) -> Pair<K?, V?>?,
): Map<K, V> = mapNotNull {
    val (k, v) = valueSelector(it) ?: return@mapNotNull null
    if (k == null || v == null) return@mapNotNull null
    k to v
}.toMap()

public inline fun <T, K : Any> Sequence<T>.associateByNotNull(
    crossinline keySelector: (T) -> K?,
): Map<K, T> = mapNotNull {
    (keySelector(it) ?: return@mapNotNull null) to it
}.toMap()

public inline fun <T, V : Any> Sequence<T>.associateWithNotNull(
    crossinline valueSelector: (T) -> V?,
): Map<T, V> = mapNotNull {
    it to (valueSelector(it) ?: return@mapNotNull null)
}.toMap()

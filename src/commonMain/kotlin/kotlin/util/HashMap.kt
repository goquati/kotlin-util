package io.github.goquati.kotlin.util

public inline fun <K, V, R : Any> Map<out K, V>.mapValuesNotNull(
    transform: (Map.Entry<K, V>) -> R?,
): Map<K, R> = mapNotNull {
    it.key to (transform(it) ?: return@mapNotNull null)
}.toMap()

public inline fun <K, V, R : Any> Map<out K, V>.mapKeysNotNull(
    transform: (Map.Entry<K, V>) -> R?,
): Map<R, V> = mapNotNull {
    (transform(it) ?: return@mapNotNull null) to it.value
}.toMap()

public fun <K, V> Iterable<Map<K, V>>.flatten(): Map<K, V> =
    flatMap { it.entries }.associate { it.key to it.value }

public fun <K, T1, T2, R> combine(
    m1: Map<K, T1>,
    m2: Map<K, T2>,
    transform: (K, T1, T2) -> R
): Map<K, R> = intersectAll(m1.keys, m2.keys).associateWith {
    transform(it, m1[it]!!, m2[it]!!)
}

public fun <K, T1, T2, T3, R> combine(
    m1: Map<K, T1>,
    m2: Map<K, T2>,
    m3: Map<K, T3>,
    transform: (K, T1, T2, T3) -> R
): Map<K, R> = intersectAll(m1.keys, m2.keys, m3.keys).associateWith {
    transform(it, m1[it]!!, m2[it]!!, m3[it]!!)
}

public fun <K, T1, T2, T3, T4, R> combine(
    m1: Map<K, T1>,
    m2: Map<K, T2>,
    m3: Map<K, T3>,
    m4: Map<K, T4>,
    transform: (K, T1, T2, T3, T4) -> R
): Map<K, R> = intersectAll(m1.keys, m2.keys, m3.keys, m4.keys).associateWith {
    transform(it, m1[it]!!, m2[it]!!, m3[it]!!, m4[it]!!)
}

public fun <K, T1, T2, T3, T4, T5, R> combine(
    m1: Map<K, T1>,
    m2: Map<K, T2>,
    m3: Map<K, T3>,
    m4: Map<K, T4>,
    m5: Map<K, T5>,
    transform: (K, T1, T2, T3, T4, T5) -> R
): Map<K, R> = intersectAll(m1.keys, m2.keys, m3.keys, m4.keys, m5.keys).associateWith {
    transform(it, m1[it]!!, m2[it]!!, m3[it]!!, m4[it]!!, m5[it]!!)
}

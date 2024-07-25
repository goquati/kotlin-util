package io.github.klahap.kotlin.util

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

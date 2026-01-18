package de.quati.kotlin.util.cache

import java.time.Instant


public fun <K: Any, V: Any> cacheBuilder(block: Cache.Builder<K, V>.() -> Unit): Cache<K, V> =
    Cache.Builder<K, V>().apply(block).build()

internal fun Long.nanosToInstant() =
    Instant.ofEpochMilli(this / 1_000_000).plusNanos(this % 1_000_000)!!
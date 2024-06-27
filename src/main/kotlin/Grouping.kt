package io.github.klahap.kotlin.util


inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMaxBy(selector: (T) -> C): Map<K, T> =
    reduce { _, acc, elem -> if (selector(acc) > selector(elem)) acc else elem }

inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMinBy(selector: (T) -> C): Map<K, T> =
    reduce { _, acc, elem -> if (selector(acc) < selector(elem)) acc else elem }

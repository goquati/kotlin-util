package de.quati.kotlin.util

public inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMaxBy(selector: (T) -> C): Map<K, T> =
    reduce { _, acc, elem -> if (selector(acc) > selector(elem)) acc else elem }

public inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMinBy(selector: (T) -> C): Map<K, T> =
    reduce { _, acc, elem -> if (selector(acc) < selector(elem)) acc else elem }

public inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMaxOf(selector: (T) -> C): Map<K, C> =
    aggregate { key, acc, element, first ->
        val c = selector(element)
        if (acc == null) c
        else maxOf(acc, c)
    }

public inline fun <T, K, C : Comparable<C>> Grouping<T, K>.eachMinOf(selector: (T) -> C): Map<K, C> =
    aggregate { key, acc, element, first ->
        val c = selector(element)
        if (acc == null) c
        else minOf(acc, c)
    }

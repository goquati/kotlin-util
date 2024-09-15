package io.github.goquati.kotlin.coroutine.util

import kotlinx.coroutines.flow.*

public suspend inline fun <T, C : Collection<T>> MutableStateFlow<C>.getAndResetCollection(defaultValue: C): C {
    return filterNotEmpty()
        .map { getAndUpdate { defaultValue } }
        .filterNotEmpty()
        .first()
}

public suspend inline fun <T> MutableStateFlow<List<T>>.getAndResetList(): List<T> = getAndResetCollection(emptyList())
public suspend inline fun <T> MutableStateFlow<Set<T>>.getAndResetSet(): Set<T> = getAndResetCollection(emptySet())

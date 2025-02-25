package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.*
import kotlinx.coroutines.flow.*


public fun <E> Flow<Result<*, E>>.filterFailure(): Flow<E> = filter { it.isFailure } .map { it.failure }
public fun <T> Flow<Result<T, *>>.filterSuccess(): Flow<T> = filter { it.isSuccess } .map { it.success }
public fun <T, E> Flow<Result<T, E>>.onEachFailure(block: (E) -> Unit): Flow<Result<T, E>> =
    onEach { if (it.isFailure) block(it.failure) }

public suspend fun <T, E> Flow<Result<T, E>>.toResultList(destination: MutableList<T> = ArrayList()): Result<List<T>, E> =
    toResultCollection(destination)

public suspend fun <T, E> Flow<Result<T, E>>.toResultSet(destination: MutableSet<T> = LinkedHashSet()): Result<Set<T>, E> =
    toResultCollection(destination)

public suspend fun <T, E, C : MutableCollection<in T>> Flow<Result<T, E>>.toResultCollection(destination: C): Result<C, E> {
    var error: E? = null
    val result = takeWhile {
        if (it.isFailure) error = it.failure
        !it.isFailure
    }.map { it.success }.toCollection(destination)
    return error?.let { Failure(it) } ?: Success(result)
}

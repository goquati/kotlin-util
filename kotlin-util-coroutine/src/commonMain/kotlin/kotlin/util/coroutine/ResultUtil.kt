package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Success
import kotlinx.coroutines.flow.*


public fun <E> Flow<Result<*, E>>.filterFailure(): Flow<E> = mapNotNull { if (it.isFailure) it.failure else null }
public fun <T> Flow<Result<T, *>>.filterSuccess(): Flow<T> = mapNotNull { if (it.isSuccess) it.success else null }
public fun <T, E> Flow<Result<T, E>>.filterSuccess(errorHandler: (E) -> Unit): Flow<T> = mapNotNull {
    if (it.isFailure) {
        errorHandler(it.failure)
        null
    } else
        it.success
}

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

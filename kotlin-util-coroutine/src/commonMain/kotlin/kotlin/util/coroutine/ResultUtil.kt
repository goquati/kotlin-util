package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.*
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

public suspend fun <T, E> Flow<Result<T, E>>.toResultListOr(
    destination: MutableList<T> = ArrayList(),
    block: (Collection<E>) -> List<T>,
): List<T> = toResultCollectionOr(destination) { block(it).toMutableList() }

public suspend fun <T, E> Flow<Result<T, E>>.toResultSetOr(
    destination: MutableSet<T> = LinkedHashSet(),
    block: (Collection<E>) -> Set<T>,
): Set<T> = toResultCollectionOr(destination) { block(it).toMutableSet() }

public suspend fun <T, E, C : MutableCollection<in T>> Flow<Result<T, E>>.toResultCollectionOr(
    destination: C,
    block: (Collection<E>) -> C,
): C {
    val errors = mutableListOf<E>()
    collect {
        when {
            it.isFailure -> errors.add(it.failure)
            else -> destination.add(it.success)
        }
    }
    if (errors.isEmpty()) return destination
    return block(errors)
}

package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.*
import kotlinx.coroutines.flow.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


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

public suspend inline fun <T, E> Flow<Result<T, E>>.toResultListOr(
    destination: MutableList<T> = ArrayList(),
    block: (Collection<E>) -> List<T>,
): List<T> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return toResultCollectionOr(destination) { block(it).toMutableList() }
}

public suspend inline fun <T, E> Flow<Result<T, E>>.toResultSetOr(
    destination: MutableSet<T> = LinkedHashSet(),
    block: (Collection<E>) -> Set<T>,
): Set<T> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return toResultCollectionOr(destination) { block(it).toMutableSet() }
}

public suspend inline fun <T, E, C : MutableCollection<in T>> Flow<Result<T, E>>.toResultCollectionOr(
    destination: C,
    block: (Collection<E>) -> C,
): C {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return toList().toResultCollectionOr(destination, block)
}

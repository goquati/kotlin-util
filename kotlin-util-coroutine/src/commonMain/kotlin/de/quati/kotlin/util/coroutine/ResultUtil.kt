package de.quati.kotlin.util.coroutine

import de.quati.kotlin.util.*
import kotlinx.coroutines.flow.*


public fun <E> Flow<Result<*, E>>.filterFailure(): Flow<E> = filterIsInstance<Failure<E>>().map { it.failure }
public fun <T> Flow<Result<T, *>>.filterSuccess(): Flow<T> = filterIsInstance<Success<T>>().map { it.value }
public fun <T, E> Flow<Result<T, E>>.onEachFailure(block: (E) -> Unit): Flow<Result<T, E>> =
    onEach { if (it is Failure) block(it.failure) }

public suspend fun <T, E> Flow<Result<T, E>>.toResultList(destination: MutableList<T> = ArrayList()): Result<List<T>, E> =
    toResultCollection(destination)

public suspend fun <T, E> Flow<Result<T, E>>.toResultSet(destination: MutableSet<T> = LinkedHashSet()): Result<Set<T>, E> =
    toResultCollection(destination)

public suspend fun <T, E, C : MutableCollection<in T>> Flow<Result<T, E>>.toResultCollection(destination: C): Result<C, E> {
    var error: E? = null
    val result = takeWhile {
        when (it) {
            is Failure -> {
                error = it.failure
                false
            }
            is Success -> true
        }
    }.map { (it as Success).value }.toCollection(destination)
    return error?.let { Failure(it) } ?: Success(result)
}

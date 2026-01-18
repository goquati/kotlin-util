package de.quati.kotlin.util.coroutine

import de.quati.kotlin.util.*
import kotlinx.coroutines.flow.*
import kotlin.Result


public fun Flow<Result<*>>.filterFailure(): Flow<Throwable> = filter { it.isFailure }.map { it.exceptionOrThrow() }
public fun <T> Flow<Result<T>>.filterSuccess(): Flow<T> = filter { it.isSuccess }.map { it.getOrThrow() }
public fun <T> Flow<Result<T>>.onEachFailure(block: (Throwable) -> Unit): Flow<Result<T>> =
    onEach { if (it.isFailure) block(it.exceptionOrThrow()) }

public suspend fun <T> Flow<Result<T>>.toResultList(destination: MutableList<T> = ArrayList()): Result<List<T>> =
    toResultCollection(destination)

public suspend fun <T> Flow<Result<T>>.toResultSet(destination: MutableSet<T> = LinkedHashSet()): Result<Set<T>> =
    toResultCollection(destination)

public suspend fun <T, C : MutableCollection<in T>> Flow<Result<T>>.toResultCollection(destination: C): Result<C> {
    var error: Throwable? = null
    val result = takeWhile {
        if (it.isFailure) error = it.exceptionOrThrow()
        !it.isFailure
    }.map { it.getOrThrow() }.toCollection(destination)
    return error?.let { Result.failure(it) } ?: Result.success(result)
}

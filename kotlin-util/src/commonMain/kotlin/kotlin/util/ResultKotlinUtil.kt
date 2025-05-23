package io.github.goquati.kotlin.util

import kotlin.Result
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun Result<*>.exceptionOrThrow(): Throwable =
    exceptionOrNull() ?: throw IllegalStateException("Expected an exception, but result was successful.")

public fun <T, E> Result<T>.mapError(block: (Throwable) -> E): io.github.goquati.kotlin.util.Result<T, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return Success(getOrElse { return Failure(block(it)) })
}

public fun <T, R> Result<T>.flatMap(block: (T) -> Result<R>): Result<R> = map { block(it) }.flatten()
public fun <T> Result<Result<T>>.flatten(): Result<T> = getOrElse { return Result.failure(it) }

public fun <T> Iterable<Result<T>>.filterSuccess(): List<T> = filter { it.isSuccess }.map { it.getOrThrow() }
public fun Iterable<Result<*>>.filterFailure(): List<Throwable> = mapNotNull { it.exceptionOrNull() }

public fun <T> Iterable<Result<T>>.onEachFailure(block: (Throwable) -> Unit): Iterable<Result<T>> =
    onEach { it.exceptionOrNull()?.let(block) }

public fun <T> Iterable<Result<T>>.toResultList(): Result<List<T>> = toResultCollection(ArrayList())
public fun <T> Iterable<Result<T>>.toResultSet(): Result<Set<T>> = toResultCollection(LinkedHashSet())
public fun <T, C : MutableCollection<in T>> Iterable<Result<T>>.toResultCollection(destination: C): Result<C> {
    var error: Throwable? = null
    val result = takeWhile {
        if (it.isFailure) error = it.exceptionOrThrow()
        !it.isFailure
    }.map { it.getOrThrow() }.toCollection(destination)
    return error?.let { Result.failure(it) } ?: Result.success(result)
}

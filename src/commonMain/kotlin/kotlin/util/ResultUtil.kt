package io.github.goquati.kotlin.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@Suppress("FunctionName")
public fun <V> Success(value: V): Result<V, Nothing> = Result(value)

@Suppress("FunctionName")
public fun <E> Failure(error: E): Result<Nothing, E> = Result(Result.Failure(error))

public fun <T> Result<T, *>.getOr(default: T): T = when {
    isFailure -> default
    else -> success
}

public inline fun <T, E> Result<T, E>.getOr(block: (E) -> T): T {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when {
        isFailure -> block(failure)
        else -> success
    }
}

public fun <E> Result<*, E>.getFailureOr(default: E): E = when {
    isFailure -> failure
    else -> default
}

public inline fun <T, E> Result<T, E>.getFailureOr(block: (T) -> E): E {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when {
        isFailure -> failure
        else -> block(success)
    }
}

public inline fun <T1, T2, E> Result<T1, E>.map(block: (T1) -> T2): Result<T2, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when {
        isFailure -> asFailure
        else -> Success(block(success))
    }
}

public inline fun <T1, T2, E> Result<T1, E>.flatMap(block: (T1) -> Result<T2, E>): Result<T2, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when {
        isFailure -> asFailure
        else -> block(success)
    }
}

public inline fun <T, E1, E2> Result<T, E1>.mapError(block: (E1) -> E2): Result<T, E2> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when {
        isFailure -> Failure(block(failure))
        else -> asSuccess
    }
}

public fun <T, E> Result<Result<T, E>, E>.flatten(): Result<T, E> = when {
    isFailure -> asFailure
    else -> success
}

public fun <T> Iterable<Result<T, *>>.filterSuccess(): List<T> = filter { it.isSuccess }.map { it.success }
public fun <E> Iterable<Result<*, E>>.filterFailure(): List<E> = filter { it.isFailure }.map { it.failure }
public fun <T, E> Iterable<Result<T, E>>.toResultList(): Result<List<T>, E> = toResultCollection(ArrayList())
public fun <T, E> Iterable<Result<T, E>>.toResultSet(): Result<Set<T>, E> = toResultCollection(LinkedHashSet())

private fun <T, E, C : MutableCollection<in T>> Iterable<Result<T, E>>.toResultCollection(destination: C): Result<C, E> {
    var error: E? = null
    val result = takeWhile {
        if (it.isFailure) error = it.failure
        !it.isFailure
    }.map { it.success }.toCollection(destination)
    return error?.let { Failure(it) } ?: Success(result)
}
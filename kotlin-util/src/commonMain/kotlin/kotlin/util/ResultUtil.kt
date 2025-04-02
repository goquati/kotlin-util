package io.github.goquati.kotlin.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun <T, E> Result<T, E>.isSuccess(): Boolean {
    contract {
        this.returns(true) implies (this@isSuccess is Success<T>)
        this.returns(false) implies (this@isSuccess is Failure<E>)
    }
    return this is Success<*>
}

public fun <T, E> Result<T, E>.isFailure(): Boolean {
    contract {
        this.returns(false) implies (this@isFailure is Success<T>)
        this.returns(true) implies (this@isFailure is Failure<E>)
    }
    return this is Failure<*>
}

public fun <T> Result<T, *>.getOr(default: T): T = when (this) {
    is Failure -> default
    is Success -> value
}

public inline fun <T, E> Result<T, E>.getOr(block: (E) -> T): T {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Failure -> block(failure)
        is Success -> value
    }
}

public fun <E> Result<*, E>.getFailureOr(default: E): E = when (this) {
    is Failure -> failure
    is Success -> default
}

public fun <T, E : Throwable> Result<T, E>.getOrThrow(): T = getOr { throw it }
public fun <T, E : Throwable> Result<T, E>.toKotlin(): kotlin.Result<T> = when (this) {
    is Failure -> kotlin.Result.failure(failure)
    is Success -> kotlin.Result.success(value)
}

public fun <T, E : Throwable> Iterable<Result<T, E>>.toKotlin(): List<kotlin.Result<T>> = map { it.toKotlin() }

public inline fun <T, E> Result<T, E>.getFailureOr(block: (T) -> E): E {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Failure -> failure
        is Success -> block(value)
    }
}

public inline fun <T1, T2, E> Result<T1, E>.map(block: (T1) -> T2): Result<T2, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Failure -> this
        is Success -> Success(block(value))
    }
}

public inline fun <T1, T2, E> Result<T1, E>.flatMap(block: (T1) -> Result<T2, E>): Result<T2, E> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Failure -> this
        is Success -> block(value)
    }
}

public inline fun <T, E1, E2> Result<T, E1>.mapError(block: (E1) -> E2): Result<T, E2> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Failure -> Failure(block(failure))
        is Success -> this
    }
}

public fun <T, E> Result<Result<T, E>, E>.flatten(): Result<T, E> = when (this) {
    is Failure -> this
    is Success -> value
}

public fun <T> Iterable<Result<T, *>>.filterSuccess(): List<T> = filterIsInstance<Success<T>>().map { it.value }
public fun <E> Iterable<Result<*, E>>.filterFailure(): List<E> =
    filterIsInstance<Failure<E>>().map { it.failure }

public fun <T, E, C : Iterable<Result<T, E>>> C.onEachFailure(block: (E) -> Unit): C =
    onEach { if (it is Failure) block(it.failure) }

public fun <T, E> Iterable<Result<T, E>>.toResultList(): Result<List<T>, E> = toResultCollection(ArrayList())
public fun <T, E> Iterable<Result<T, E>>.toResultSet(): Result<Set<T>, E> = toResultCollection(LinkedHashSet())
public fun <T, E, C : MutableCollection<in T>> Iterable<Result<T, E>>.toResultCollection(destination: C): Result<C, E> {
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

package io.github.goquati.kotlin.util

import kotlin.jvm.JvmInline

public sealed interface Result<out T, out E>

@JvmInline
public value class Success<T> public constructor(public val value: T) : Result<T, Nothing> {
    override fun toString(): String = "Success($value)"
}

@JvmInline
public value class Failure<E> public constructor(public val failure: E) : Result<Nothing, E> {
    override fun toString(): String = "Failure($failure)"
}

public val <T> Result<T, *>.successOrNull: T? get() = (this as? Success<T>)?.value
public val <E> Result<*, E>.failureOrNull: E? get() = (this as? Failure<E>)?.failure
public val <T> T.failure: Failure<T> get() = Failure(this)
public val <T> T.success: Success<T> get() = Success(this)

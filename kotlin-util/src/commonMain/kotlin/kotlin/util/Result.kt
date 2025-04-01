package io.github.goquati.kotlin.util

import kotlin.jvm.JvmInline

public sealed interface Result<out T, out E> {
    public val isSuccess: Boolean get() = this is Success<*>
    public val isFailure: Boolean get() = this is Failure<*>

    public val successOrNull: T? get() = (this as? Success<T>)?.value
    public val failureOrNull: E? get() = (this as? Failure<E>)?.failure
}

@JvmInline
public value class Success<T> public constructor(public val value: T) : Result<T, Nothing> {
    override fun toString(): String = "Success($value)"
}

@JvmInline
public value class Failure<E> public constructor(public val failure: E) : Result<Nothing, E> {
    override fun toString(): String = "Failure($failure)"
}

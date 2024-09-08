package io.github.goquati.kotlin.util

import kotlin.jvm.JvmInline

@JvmInline
public value class Result<out T, out E> internal constructor(
    private val value: Any?,
) {
    public val isSuccess: Boolean get() = value !is Failure<*>
    public val isFailure: Boolean get() = value is Failure<*>

    @Suppress("UNCHECKED_CAST")
    public val asSuccess: Result<T, Nothing>
        get() {
            checkSuccess()
            return this as Result<T, Nothing>
        }

    @Suppress("UNCHECKED_CAST")
    public val asFailure: Result<Nothing, E>
        get() {
            checkFailure()
            return this as Result<Nothing, E>
        }

    @Suppress("UNCHECKED_CAST")
    public val success: T
        get() {
            checkSuccess()
            return value as T
        }

    @Suppress("UNCHECKED_CAST")
    public val failure: E
        get() {
            checkFailure()
            return (value as Failure<E>).value
        }

    public val successOrNull: T? get() = if (isFailure) null else success
    public val failureOrNull: E? get() = if (isFailure) failure else null

    override fun toString(): String = when (value) {
        is Failure<*> -> value.toString()
        else -> "Success($value)"
    }

    private fun checkSuccess() {
        if (isFailure) throw IllegalStateException("Cannot retrieve success, the result is a failure.")
    }

    private fun checkFailure() {
        if (isSuccess) throw IllegalStateException("Cannot retrieve failure, the result is a success.")
    }

    @JvmInline
    internal value class Failure<out F>(val value: F) {
        override fun toString(): String = "Failure($value)"
    }
}


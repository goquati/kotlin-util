package io.github.goquati.kotlin.util

import io.github.goquati.kotlin.util.TriState.Null
import io.github.goquati.kotlin.util.TriState.Undefined
import io.github.goquati.kotlin.util.TriState.Value
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

public sealed interface TriState<out T> {
    @JvmInline
    public value class Value<T : Any>(public val value: T) : TriState<T>
    public data object Null : TriState<Nothing>
    public data object Undefined : TriState<Nothing>

    public companion object {
        public fun <T : Any> of(value: T?): TriState<T> = when (value) {
            null -> Null
            else -> Value(value)
        }
    }
}


public val <T> TriState<T>.isPresent: Boolean get() = this is Value
public val <T> TriState<T>.isNull: Boolean get() = this is Null
public val <T> TriState<T>.isUndefined: Boolean get() = this is Undefined

public fun <T> TriState<T>.getOrNull(): T? = when (this) {
    is TriState.Value -> value
    else -> null
}

public fun <T> TriState<T>.getOr(default: T): T = when (this) {
    is TriState.Value -> value
    else -> default
}

public inline fun <T> TriState<T>.getOr(block: () -> T): T {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        Null -> block()
        Undefined -> block()
        is TriState.Value -> value
    }
}
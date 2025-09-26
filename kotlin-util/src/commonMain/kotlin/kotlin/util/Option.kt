package io.github.goquati.kotlin.util

import io.github.goquati.kotlin.util.Option.Some
import io.github.goquati.kotlin.util.Option.Undefined
import kotlin.jvm.JvmInline

public sealed interface Option<out T> {
    @JvmInline
    public value class Some<T>(public val value: T) : Option<T>
    public data object Undefined : Option<Nothing>
}

public val Option<*>.isSome: Boolean get() = this is Some
public val Option<*>.isUndefined: Boolean get() = this is Undefined
public fun <T> Option<T>.takeSome(): Some<T>? = this as? Some
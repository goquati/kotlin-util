package io.github.goquati.kotlin.util

public fun <T : Any> SingleResult<T>.getOrNull(): T? = when (this) {
    is SingleResult.Success -> data
    SingleResult.None -> null
    SingleResult.TooMany -> null
}

public fun <T> Sequence<T>.singleResult(): SingleResult<T> = take(2).toList().singleResult()
public fun <T> Iterable<T>.singleResult(): SingleResult<T> = take(2).toList().singleResult()
public fun <T> Collection<T>.singleResult(): SingleResult<T> = when (size) {
    0 -> SingleResult.None
    1 -> SingleResult.Success(single())
    else -> SingleResult.TooMany
}

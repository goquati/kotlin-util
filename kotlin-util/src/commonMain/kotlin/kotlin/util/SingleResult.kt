package io.github.goquati.kotlin.util

public sealed interface SingleResult<out T> {
    public fun getOrThrow(msg: String? = null): T = when (this) {
        is Success -> data
        None -> throw QuatiException.NotFound(
            when (msg) {
                null -> "nothing found"
                else -> "$msg - nothing found"
            }
        )

        TooMany -> throw QuatiException.Conflict(
            when (msg) {
                null -> "multiple matches found"
                else -> "$msg - multiple matches found"
            }
        )
    }

    public data object None : SingleResult<Nothing>
    public data class Success<T>(public val data: T) : SingleResult<T>
    public data object TooMany : SingleResult<Nothing>
}

package de.quati.kotlin.util

public fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }

public fun String.ensurePrefix(prefix: String): String = when {
    startsWith(prefix) -> this
    else -> "$prefix$this"
}

public fun String.ensureSuffix(suffix: String): String = when {
    endsWith(suffix) -> this
    else -> "$this$suffix"
}

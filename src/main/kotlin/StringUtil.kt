package io.github.klahap.kotlin.util

public fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }

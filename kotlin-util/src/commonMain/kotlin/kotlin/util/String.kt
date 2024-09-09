package io.github.goquati.kotlin.util

public fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }

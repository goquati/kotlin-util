package io.github.goquati.kotlin.util

public inline fun <reified T : Enum<T>> valueOf(
    value: String,
    ignoreCase: Boolean = false,
): T = enumValues<T>().first { value.equals(it.name, ignoreCase = ignoreCase) }

public inline fun <reified T : Enum<T>> valueOfOrNull(
    value: String,
    ignoreCase: Boolean = false,
): T? = enumValues<T>().firstOrNull { value.equals(it.name, ignoreCase = ignoreCase) }

package io.github.goquati.kotlin.util


public inline fun <reified T : Any> Any.asOrNull(): T? = this as? T

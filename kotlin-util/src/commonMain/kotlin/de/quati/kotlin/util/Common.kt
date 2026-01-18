package de.quati.kotlin.util


public inline fun <reified T : Any> Any.asOrNull(): T? = this as? T

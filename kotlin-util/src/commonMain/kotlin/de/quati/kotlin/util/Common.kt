package de.quati.kotlin.util

import kotlin.contracts.contract


public inline fun <reified T : Any> Any.asOrNull(): T? {
    contract {
        returnsNotNull() implies (this@asOrNull is T)
    }
    return this as? T
}

package de.quati.kotlin.util.serialization

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

public fun JsonObject.toKotlinValue(): Map<String, Any?> = mapValues { it.value.toKotlinValue() }
public fun JsonArray.toKotlinValue(): List<Any?> = map { it.toKotlinValue() }
public fun JsonElement.toKotlinValue(): Any? = when (this) {
    is JsonObject -> toKotlinValue()
    is JsonArray -> toKotlinValue()
    JsonNull -> null
    is JsonPrimitive -> if (isString)
        content
    else
        booleanOrNull
            ?: intOrNull
            ?: longOrNull
            ?: double
}

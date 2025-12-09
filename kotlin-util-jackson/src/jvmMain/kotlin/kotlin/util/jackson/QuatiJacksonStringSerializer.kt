package de.quati.kotlin.util.jackson

import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import kotlin.reflect.KClass

public interface QuatiJacksonStringSerializer<T : Any> {
    public val clazz: KClass<T>
    public val serialize: (T) -> String
    public val deserialize: (it: String) -> T

    public fun jSerializer(): ValueSerializer<T> = object : ValueSerializer<T>() {
        override fun serialize(value: T, gen: JsonGenerator, ctxt: SerializationContext) {
            val sValue = serialize(value)
            gen.writeString(sValue)
        }
    }

    public fun jDeserializer(): ValueDeserializer<T> = object : ValueDeserializer<T>() {
        override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): T? {
            val sValue = parser.valueAsString ?: return null
            return deserialize(sValue)
        }
    }
}
package io.github.goquati.kotlin.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

public interface QuatiKotlinxStringSerializer<T : Any> : KSerializer<T> {
    public val clazz: KClass<T>
    public val name: String get() = "${clazz.simpleName}Serializer"
    public val serialize: (T) -> String
    public val deserialize: (it: String) -> T

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        val sValue = serialize(value)
        return encoder.encodeString(sValue)
    }

    override fun deserialize(decoder: Decoder): T {
        val sValue = decoder.decodeString()
        return deserialize(sValue)
    }
}
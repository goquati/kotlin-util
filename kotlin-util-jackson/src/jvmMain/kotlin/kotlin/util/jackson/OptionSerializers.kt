package io.github.goquati.kotlin.util.jackson

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.goquati.kotlin.util.Option
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import tools.jackson.databind.ser.Serializers
import tools.jackson.databind.type.ReferenceType

internal object OptionSerializers : Serializers.Base() {
    override fun findReferenceSerializer(
        config: SerializationConfig,
        type: ReferenceType,
        beanDescRef: BeanDescription.Supplier?,
        formatOverrides: JsonFormat.Value?,
        contentTypeSerializer: TypeSerializer?,
        contentValueSerializer: ValueSerializer<in Any>?
    ): ValueSerializer<*>? = if (Option::class.java.isAssignableFrom(type.rawClass)) {
        val staticTyping = contentTypeSerializer == null && config.isEnabled(MapperFeature.USE_STATIC_TYPING)
        OptionSerializer(
            type, staticTyping,
            contentTypeSerializer, contentValueSerializer
        )
    } else null
}
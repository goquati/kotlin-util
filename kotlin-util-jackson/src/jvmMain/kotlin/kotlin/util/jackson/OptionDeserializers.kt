package de.quati.kotlin.util.jackson

import de.quati.kotlin.util.Option
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.deser.Deserializers
import tools.jackson.databind.jsontype.TypeDeserializer
import tools.jackson.databind.type.ReferenceType

internal object OptionDeserializers : Deserializers.Base() {
    override fun findReferenceDeserializer(
        refType: ReferenceType,
        config: DeserializationConfig?,
        beanDescRef: BeanDescription.Supplier?,
        contentTypeDeserializer: TypeDeserializer?,
        contentDeserializer: ValueDeserializer<*>?
    ): ValueDeserializer<*>? = if (refType.hasRawClass(Option::class.java)) OptionDeserializer(
        refType,
        null,
        contentTypeDeserializer,
        contentDeserializer
    ) else null

    override fun hasDeserializerFor(
        config: DeserializationConfig?,
        valueType: Class<*>?
    ): Boolean = valueType?.isAssignableFrom(Option::class.java) ?: false
}
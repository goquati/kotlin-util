package de.quati.kotlin.util.jackson

import de.quati.kotlin.util.Option
import tools.jackson.core.JsonGenerator
import tools.jackson.core.io.SerializedString
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.BeanPropertyWriter
import tools.jackson.databind.ser.bean.UnwrappingBeanPropertyWriter
import tools.jackson.databind.util.NameTransformer

internal class UnwrappingOptionBeanPropertyWriter : UnwrappingBeanPropertyWriter {
    constructor(
        base: BeanPropertyWriter,
        transformer: NameTransformer?
    ) : super(base, transformer)

    constructor(
        base: UnwrappingBeanPropertyWriter,
        transformer: NameTransformer?, name: SerializedString?
    ) : super(base, transformer, name)

    override fun _new(transformer: NameTransformer?, newName: SerializedString?): UnwrappingOptionBeanPropertyWriter =
        UnwrappingOptionBeanPropertyWriter(this, transformer, newName)

    override fun serializeAsProperty(bean: Any?, gen: JsonGenerator?, prov: SerializationContext?) {
        val value = get(bean)
        if (Option.Undefined == value || (_nullSerializer == null && value == null))
            return
        super.serializeAsProperty(bean, gen, prov)
    }
}
package de.quati.kotlin.util.jackson

import de.quati.kotlin.util.Option
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.PropertyName
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.BeanPropertyWriter
import tools.jackson.databind.util.NameTransformer

internal class OptionBeanPropertyWriter : BeanPropertyWriter {
    constructor(base: BeanPropertyWriter) : super(base)
    constructor(base: BeanPropertyWriter, newName: PropertyName) : super(base, newName)

    override fun _new(newName: PropertyName): OptionBeanPropertyWriter = OptionBeanPropertyWriter(this, newName)
    override fun unwrappingWriter(unwrapper: NameTransformer?): UnwrappingOptionBeanPropertyWriter =
        UnwrappingOptionBeanPropertyWriter(this, unwrapper)

    override fun serializeAsProperty(bean: Any?, g: JsonGenerator?, ctxt: SerializationContext?) {
        val value = get(bean)
        if (value == Option.Undefined || (_nullSerializer == null && value == null))
            return
        super.serializeAsProperty(bean, g, ctxt)
    }
}
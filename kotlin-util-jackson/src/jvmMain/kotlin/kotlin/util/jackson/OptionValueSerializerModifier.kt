package io.github.goquati.kotlin.util.jackson

import io.github.goquati.kotlin.util.Option
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.ser.BeanPropertyWriter
import tools.jackson.databind.ser.ValueSerializerModifier

internal class OptionValueSerializerModifier : ValueSerializerModifier() {
    override fun changeProperties(
        config: SerializationConfig,
        beanDesc: BeanDescription.Supplier,
        beanProperties: MutableList<BeanPropertyWriter>
    ): MutableList<BeanPropertyWriter> {
        for (i in beanProperties.indices) {
            val writer = beanProperties[i]
            val type = writer.type
            if (type.isTypeOrSubTypeOf(Option::class.java))
                beanProperties[i] = OptionBeanPropertyWriter(writer)
        }
        return beanProperties
    }
}
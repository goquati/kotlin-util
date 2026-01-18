package de.quati.kotlin.util.jackson

import de.quati.kotlin.util.Option
import de.quati.kotlin.util.takeSome
import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.deser.ValueInstantiator
import tools.jackson.databind.deser.std.ReferenceTypeDeserializer
import tools.jackson.databind.jsontype.TypeDeserializer
import tools.jackson.databind.type.ReferenceType

internal class OptionDeserializer(
    fullType: JavaType,
    inst: ValueInstantiator?,
    typeDeser: TypeDeserializer?,
    deser: ValueDeserializer<*>?
) : ReferenceTypeDeserializer<Option<*>>(fullType, inst, typeDeser, deser) {
    private val isStringDeserializer: Boolean = (fullType is ReferenceType
            && fullType.referencedType != null
            && fullType.referencedType.isTypeOrSubTypeOf(String::class.java))

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Option<*> {
        val t = p.currentToken()
        if (t == JsonToken.VALUE_STRING && !isStringDeserializer && p.string.trim().isEmpty())
            return Option.Undefined
        return super.deserialize(p, ctxt)
    }

    override fun withResolved(
        typeDeser: TypeDeserializer?,
        valueDeser: ValueDeserializer<*>?
    ): OptionDeserializer = OptionDeserializer(_fullType, _valueInstantiator, typeDeser, valueDeser)

    override fun getAbsentValue(ctxt: DeserializationContext): Option.Undefined = Option.Undefined
    override fun getNullValue(ctxt: DeserializationContext): Option.Some<Nothing?> = Option.Some(null)
    override fun getEmptyValue(ctxt: DeserializationContext): Option.Undefined = Option.Undefined
    override fun referenceValue(contents: Any?): Option.Some<Any?> = Option.Some(contents)
    override fun updateReference(reference: Option<*>, contents: Any?): Option.Some<Any?> = Option.Some(contents)
    override fun supportsUpdate(config: DeserializationConfig): Boolean = true
    override fun getReferenced(reference: Option<*>): Any? = reference.takeSome()?.value
}
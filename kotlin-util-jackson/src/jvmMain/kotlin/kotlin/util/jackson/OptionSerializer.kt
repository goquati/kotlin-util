package io.github.goquati.kotlin.util.jackson

import io.github.goquati.kotlin.util.Option
import io.github.goquati.kotlin.util.isSome
import io.github.goquati.kotlin.util.takeSome
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.jsontype.TypeSerializer
import tools.jackson.databind.ser.std.ReferenceTypeSerializer
import tools.jackson.databind.type.ReferenceType
import tools.jackson.databind.util.NameTransformer

internal class OptionSerializer : ReferenceTypeSerializer<Option<*>> {
    constructor(
        fullType: ReferenceType,
        staticTyping: Boolean,
        vts: TypeSerializer?,
        ser: ValueSerializer<Any>?
    ) : super(fullType, staticTyping, vts, ser)

    private constructor(
        base: OptionSerializer,
        property: BeanProperty?,
        vts: TypeSerializer?,
        valueSer: ValueSerializer<*>?,
        unwrapper: NameTransformer?,
        suppressableValue: Any?
    ) : super(base, property, vts, valueSer, unwrapper, suppressableValue, false)

    override fun withResolved(
        prop: BeanProperty?,
        vts: TypeSerializer?,
        valueSer: ValueSerializer<*>?,
        unwrapper: NameTransformer?
    ): ReferenceTypeSerializer<Option<*>> =
        OptionSerializer(this, prop, vts, valueSer, unwrapper, _suppressableValue)

    override fun withContentInclusion(
        suppressableValue: Any?,
        suppressNulls: Boolean,
    ): ReferenceTypeSerializer<Option<*>> =
        OptionSerializer(this, _property, _valueTypeSerializer, _valueSerializer, _unwrapper, suppressableValue)

    override fun _isValuePresent(value: Option<*>): Boolean = value.isSome
    override fun _getReferenced(value: Option<*>): Any? = value.takeSome()?.value
    override fun _getReferencedIfPresent(value: Option<*>): Any? = value.takeSome()?.value
}
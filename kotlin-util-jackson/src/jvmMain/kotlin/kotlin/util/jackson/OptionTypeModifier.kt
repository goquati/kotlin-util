package io.github.goquati.kotlin.util.jackson

import io.github.goquati.kotlin.util.Option
import tools.jackson.databind.JavaType
import tools.jackson.databind.type.ReferenceType
import tools.jackson.databind.type.TypeBindings
import tools.jackson.databind.type.TypeFactory
import tools.jackson.databind.type.TypeModifier
import java.lang.reflect.Type

internal object OptionTypeModifier : TypeModifier() {
    override fun modifyType(
        type: JavaType,
        jdkType: Type?,
        bindings: TypeBindings?,
        typeFactory: TypeFactory?,
    ): JavaType? = when {
        type.isReferenceType || type.isContainerType -> type
        type.rawClass == Option::class.java -> {
            val refType = type.containedTypeOrUnknown(0)
            ReferenceType.upgradeFrom(type, refType)
        }

        else -> type
    }
}
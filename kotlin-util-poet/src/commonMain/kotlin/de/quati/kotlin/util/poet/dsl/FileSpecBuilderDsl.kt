package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec


public fun FileSpec.Builder.addObject(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(TypeSpec.objectBuilder(name).apply(block).build())

public fun FileSpec.Builder.addInterface(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(TypeSpec.interfaceBuilder(name).apply(block).build())

public fun FileSpec.Builder.addClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): FileSpec.Builder = addType(buildClass(name = name, block = block))

public fun FileSpec.Builder.addProperty(name: String, type: TypeName, block: PropertySpec.Builder.() -> Unit): FileSpec.Builder =
    addProperty(PropertySpec.builder(name = name, type = type).apply(block).build())

public fun FileSpec.Builder.addFunction(
    name: String,
    block: FunSpec.Builder.() -> Unit,
): FileSpec.Builder = addFunction(buildFunction(name = name, block = block))

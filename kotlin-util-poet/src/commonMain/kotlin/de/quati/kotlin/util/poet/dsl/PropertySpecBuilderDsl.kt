package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec


public fun PropertySpec.Builder.initializer(
    block: CodeBlock.Builder.() -> Unit,
): PropertySpec.Builder = initializer(CodeBlock.builder().apply(block).build())

public fun PropertySpec.Builder.getter(block: FunSpec.Builder.() -> Unit): PropertySpec.Builder =
    getter(FunSpec.getterBuilder().apply(block).build())

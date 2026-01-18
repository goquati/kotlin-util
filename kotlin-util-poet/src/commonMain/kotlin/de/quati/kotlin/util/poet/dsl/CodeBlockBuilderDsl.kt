package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.CodeBlock


public fun CodeBlock.Builder.addControlFlow(
    controlFlow: String,
    vararg args: Any,
    block: CodeBlock.Builder.() -> Unit
): CodeBlock.Builder = apply {
    beginControlFlow(controlFlow, *args)
    block()
    endControlFlow()
}

public fun CodeBlock.Builder.indent(block: CodeBlock.Builder.() -> Unit): CodeBlock.Builder = apply {
    indent()
    block()
    unindent()
}

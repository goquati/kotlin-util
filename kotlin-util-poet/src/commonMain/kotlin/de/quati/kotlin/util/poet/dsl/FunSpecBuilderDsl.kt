package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import de.quati.kotlin.util.poet.PackageName


public fun FunSpec.Builder.addAnnotation(
    className: ClassName,
    block: AnnotationSpec.Builder.() -> Unit = {},
): FunSpec.Builder = addAnnotation(buildAnnotationSpec(className, block))

public fun FunSpec.Builder.addAnnotation(
    packageName: String,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): FunSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

public fun FunSpec.Builder.addAnnotation(
    packageName: PackageName,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): FunSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

public fun FunSpec.Builder.addParameter(
    name: String,
    type: TypeName,
    block: ParameterSpec.Builder.() -> Unit,
): FunSpec.Builder = addParameter(ParameterSpec.builder(name, type).apply(block).build())

public fun FunSpec.Builder.addCode(
    block: CodeBlock.Builder.() -> Unit
): FunSpec.Builder = addCode(CodeBlock.builder().apply(block).build())

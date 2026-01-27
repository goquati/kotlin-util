package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import de.quati.kotlin.util.poet.PackageName


public fun buildFileSpec(
    packageName: PackageName,
    name: String,
    block: FileSpec.Builder.() -> Unit,
): FileSpec = FileSpec.builder(packageName = packageName.name, fileName = name).apply(block).build()

public fun buildObject(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.objectBuilder(name).apply(block).build()

public fun buildEnum(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.enumBuilder(name).apply(block).build()

public fun buildInterface(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.interfaceBuilder(name).apply(block).build()

public fun buildClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.classBuilder(name).apply(block).build()

public fun buildDataClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.classBuilder(name).apply { addModifiers(KModifier.DATA) }.apply(block).build()

public fun buildValueClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec = TypeSpec.classBuilder(name).apply {
    addModifiers(KModifier.VALUE)
    addAnnotation(ClassName("kotlin.jvm", "JvmInline"))
    block()
}.build()

public fun buildFunction(
    name: String,
    block: FunSpec.Builder.() -> Unit,
): FunSpec = FunSpec.builder(name).apply(block).build()

public fun buildAnnotationSpec(
    packageName: PackageName,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): AnnotationSpec = AnnotationSpec.builder(packageName.className(className)).apply(block).build()

public fun buildAnnotationSpec(
    packageName: String,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): AnnotationSpec = AnnotationSpec.builder(ClassName(packageName, className)).apply(block).build()

public fun buildAnnotationSpec(
    className: ClassName,
    block: AnnotationSpec.Builder.() -> Unit = {},
): AnnotationSpec = AnnotationSpec.builder(className).apply(block).build()

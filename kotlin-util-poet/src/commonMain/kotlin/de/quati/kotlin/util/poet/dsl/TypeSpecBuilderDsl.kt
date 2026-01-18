package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import de.quati.kotlin.util.poet.PackageName


public fun TypeSpec.Builder.addClass(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec.Builder = addType(buildClass(name = name, block = block))

public fun TypeSpec.Builder.addProperty(
    name: String,
    type: TypeName,
    block: PropertySpec.Builder.() -> Unit
): TypeSpec.Builder =
    addProperty(PropertySpec.builder(name = name, type = type).apply(block).build())

public fun TypeSpec.Builder.addEnumConstant(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec.Builder = addEnumConstant(name, TypeSpec.anonymousClassBuilder().apply(block).build())

public fun TypeSpec.Builder.primaryConstructor(
    block: FunSpec.Builder.() -> Unit,
): TypeSpec.Builder = primaryConstructor(FunSpec.constructorBuilder().apply(block).build())

public fun TypeSpec.Builder.addCompanionObject(
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec.Builder = addType(TypeSpec.companionObjectBuilder().apply(block).build())

public fun TypeSpec.Builder.addObject(
    name: String,
    block: TypeSpec.Builder.() -> Unit,
): TypeSpec.Builder = addType(TypeSpec.objectBuilder(name).apply(block).build())

public fun TypeSpec.Builder.addFunction(
    name: String,
    block: FunSpec.Builder.() -> Unit,
): TypeSpec.Builder = addFunction(buildFunction(name = name, block = block))

public fun TypeSpec.Builder.addInitializerBlock(block: CodeBlock.Builder.() -> Unit): TypeSpec.Builder =
    addInitializerBlock(CodeBlock.builder().apply(block).build())

public fun TypeSpec.Builder.addAnnotation(
    className: ClassName,
    block: AnnotationSpec.Builder.() -> Unit = {},
): TypeSpec.Builder = addAnnotation(buildAnnotationSpec(className, block))

public fun TypeSpec.Builder.addAnnotation(
    packageName: String,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): TypeSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

public fun TypeSpec.Builder.addAnnotation(
    packageName: PackageName,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): TypeSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

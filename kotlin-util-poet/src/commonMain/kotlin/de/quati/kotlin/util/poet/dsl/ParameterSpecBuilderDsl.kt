package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import de.quati.kotlin.util.poet.PackageName


public fun ParameterSpec.Builder.addAnnotation(
    className: ClassName,
    block: AnnotationSpec.Builder.() -> Unit = {},
): ParameterSpec.Builder = addAnnotation(buildAnnotationSpec(className, block))

public fun ParameterSpec.Builder.addAnnotation(
    packageName: String,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): ParameterSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

public fun ParameterSpec.Builder.addAnnotation(
    packageName: PackageName,
    className: String,
    block: AnnotationSpec.Builder.() -> Unit = {},
): ParameterSpec.Builder = addAnnotation(buildAnnotationSpec(packageName, className, block))

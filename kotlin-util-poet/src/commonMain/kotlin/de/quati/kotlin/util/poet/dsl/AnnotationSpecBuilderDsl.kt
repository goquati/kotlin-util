package de.quati.kotlin.util.poet.dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.buildCodeBlock


public fun AnnotationSpec.Builder.addStringArrayMember(
    name: String,
    values: List<String>,
): AnnotationSpec.Builder = apply {
    addMember(buildCodeBlock {
        add("%L = [", name)
        values.forEachIndexed { i, v -> add("%L%S", if (i == 0) "" else ", ", v) }
        add("]")
    })
}
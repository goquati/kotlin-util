package de.quati.kotlin.util.poet

import com.squareup.kotlinpoet.ClassName


@JvmInline
public value class PackageName(public val name: String) {
    override fun toString(): String = name
    public val parts: List<String> get() = name.split(".")
    public operator fun plus(subPackage: String): PackageName = PackageName("$name.$subPackage")

    public fun className(vararg simpleNames: String): ClassName = ClassName(name, *simpleNames)
    public fun className(simpleNames: List<String>): ClassName = ClassName(name, simpleNames)
}
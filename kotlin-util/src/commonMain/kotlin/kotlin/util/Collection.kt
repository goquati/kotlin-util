package io.github.goquati.kotlin.util

public fun <T> Collection<T>.takeIfNotEmpty(): Collection<T>? =
    takeIf { it.isNotEmpty() }

public fun <T> Collection<T>.containsAny(elements: Iterable<T>): Boolean =
    elements.any { contains(it) }

public fun <T> Collection<T>.containsAny(vararg elements: T): Boolean =
    elements.any { contains(it) }

public fun <T> Collection<Collection<T>>.intersectAll(): Set<T> =
    fold(flatten().toSet()) { x, y -> x.intersect(y.toSet()) }

public fun <T> intersectAll(vararg args: Collection<T>): Set<T> = args.toList().intersectAll()

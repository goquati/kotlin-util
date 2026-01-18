package de.quati.kotlin.util.poet

public class NameConflictResolver(
    private val separator: String = "_",
    forbidden: Iterable<String> = kotlinKeywords,
) {
    private val forbiddenNames: MutableSet<String> = forbidden.toMutableSet()

    public fun resolve(name: String): String = name.makeDifferent(
        blackList = forbiddenNames,
        separator = separator,
    ).also {
        forbiddenNames += it
    }

    public fun forbid(name: String) {
        forbiddenNames += name
    }

    public fun forbidAll(names: Iterable<String>) {
        forbiddenNames += names
    }
}
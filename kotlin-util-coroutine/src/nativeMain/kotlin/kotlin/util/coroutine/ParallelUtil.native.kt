package de.quati.kotlin.util.coroutine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual class ConcurrentQuatiMap<K : Any, V : Any> private constructor(
    private val simple: ConcurrentQuatiMapSimple<K, V>,
) : IConcurrentQuatiMap<K, V> by simple {
    public actual constructor() : this(ConcurrentQuatiMapSimple())
}

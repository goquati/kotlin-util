package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.util.associateByNotNull
import io.github.goquati.kotlin.util.associateWithNotNull
import io.github.goquati.kotlin.util.groupByNotNull
import kotlinx.coroutines.flow.*


public suspend fun <T> Flow<T>.isEmpty(): Boolean = take(1).count() == 0
public suspend fun <T> Flow<T>.isNotEmpty(): Boolean = !isEmpty()

public fun <T, C : Collection<T>> Flow<C>.filterNotEmpty(): Flow<C> = filter { !it.isEmpty() }

public suspend inline fun <T, K, V> Flow<T>.associate(
    crossinline block: (T) -> Pair<K, V>,
): Map<K, V> = toList().associate(block)

public suspend inline fun <T, K> Flow<T>.associateBy(
    crossinline keySelector: (T) -> K,
): Map<K, T> = toList().associateBy(keySelector)

public suspend inline fun <T, V> Flow<T>.associateWith(
    crossinline valueSelector: (T) -> V,
): Map<T, V> = toList().associateWith(valueSelector)

public suspend inline fun <T, K : Any, V : Any> Flow<T>.associateNotNull(
    crossinline valueSelector: (T) -> Pair<K?, V?>?,
): Map<K, V> = mapNotNull {
    val (k, v) = valueSelector(it) ?: return@mapNotNull null
    (k ?: return@mapNotNull null) to (v ?: return@mapNotNull null)
}.toList().toMap()

public suspend inline fun <T, K : Any> Flow<T>.associateByNotNull(
    crossinline keySelector: (T) -> K?,
): Map<K, T> = toList().associateByNotNull(keySelector)

public suspend inline fun <T, V : Any> Flow<T>.associateWithNotNull(
    crossinline valueSelector: (T) -> V?,
): Map<T, V> = toList().associateWithNotNull(valueSelector)


public suspend inline fun <T, K> Flow<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> =
    toList().groupBy(keySelector)

public suspend inline fun <T, K, V> Flow<T>.groupBy(
    keySelector: (T) -> K,
    valueTransform: (T) -> V,
): Map<K, List<V>> = toList().groupBy(keySelector, valueTransform)

public suspend inline fun <T, K : Any> Flow<T>.groupByNotNull(keySelector: (T) -> K?): Map<K, List<T>> =
    toList().groupByNotNull(keySelector)

public suspend inline fun <T, K : Any, V : Any> Flow<T>.groupByNotNull(
    keySelector: (T) -> K?,
    valueTransform: (T) -> V?,
): Map<K, List<V>> = toList().groupByNotNull(keySelector, valueTransform)

context(collector: FlowCollector<T>)
public suspend fun <T> Iterable<T>.emitAll() {
    forEach { collector.emit(it) }
}

context(collector: FlowCollector<T>)
public suspend fun <T> Flow<T>.emitAll() {
    collector.emitAll(this)
}

public data class WithIsLastValue<out T>(public val isLast: Boolean, public val value: T)
public data class IndexedWithIsLastValue<out T>(public val index: Int, public val isLast: Boolean, public val value: T)

public fun <T> Flow<T>.withIsLast(): Flow<WithIsLastValue<T>> = flow {
    var prev: Result<T, Unit> = Failure(Unit)
    collect { current ->
        when (val p = prev) {
            is Failure -> Unit
            is Success -> emit(WithIsLastValue(isLast = false, value = p.value))
        }
        prev = Success(current)
    }
    when (val p = prev) {
        is Failure -> Unit
        is Success -> emit(WithIsLastValue(isLast = true, value = p.value))
    }
}

public fun <T> Flow<T>.withIndexedAndIsLast(): Flow<IndexedWithIsLastValue<T>> = withIsLast().withIndex().map {
    IndexedWithIsLastValue(index = it.index, value = it.value.value, isLast = it.value.isLast)
}

package de.quati.kotlin.util.coroutine

import de.quati.kotlin.util.Failure
import de.quati.kotlin.util.Result
import de.quati.kotlin.util.Success
import de.quati.kotlin.util.associateByNotNull
import de.quati.kotlin.util.associateWithNotNull
import de.quati.kotlin.util.groupByNotNull
import kotlinx.coroutines.flow.*

public fun <T> Flow<T>?.orEmpty(): Flow<T> = this ?: flowOf()

public suspend fun <T> Flow<T>.isEmpty(): Boolean = take(1).count() == 0
public suspend fun <T> Flow<T>.isNotEmpty(): Boolean = take(1).count() == 1

public suspend fun <T> Flow<T>.any(): Boolean = take(1).count() == 1
public suspend fun <T> Flow<T>.none(): Boolean = take(1).count() == 0

public suspend inline fun <T> Flow<T>.any(crossinline predicate: (T) -> Boolean): Boolean =
    filter(predicate).take(1).count() != 0

public suspend inline fun <T> Flow<T>.all(crossinline predicate: suspend (T) -> Boolean): Boolean =
    filterNot(predicate).take(1).count() == 0

public suspend inline fun <T> Flow<T>.none(crossinline predicate: suspend (T) -> Boolean): Boolean =
    filter(predicate).take(1).count() == 0

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

public suspend inline fun <T> Flow<T>.reduceOrNull(
    noinline operation: suspend (acc: T, value: T) -> T
): T? = runningReduce(operation).lastOrNull()

public suspend inline fun <T, R : Comparable<R>> Flow<T>.maxByOrNull(crossinline selector: suspend (T) -> R): T? =
    map { it to selector(it) }
        .runningReduce { acc, value -> maxOf(acc, value, compareBy { it.second }) }
        .map { it.first }
        .lastOrNull()

public suspend inline fun <T, R : Comparable<R>> Flow<T>.maxOfOrNull(crossinline selector: suspend (T) -> R): R? =
    map { it to selector(it) }
        .runningReduce { acc, value -> maxOf(acc, value, compareBy { it.second }) }
        .map { it.second }
        .lastOrNull()

public suspend inline fun <T, R : Comparable<R>> Flow<T>.minByOrNull(crossinline selector: suspend (T) -> R): T? =
    map { it to selector(it) }
        .runningReduce { acc, value -> minOf(acc, value, compareBy { it.second }) }
        .map { it.first }
        .lastOrNull()

public suspend inline fun <T, R : Comparable<R>> Flow<T>.minOfOrNull(crossinline selector: suspend (T) -> R): R? =
    map { it to selector(it) }
        .runningReduce { acc, value -> minOf(acc, value, compareBy { it.second }) }
        .map { it.second }
        .lastOrNull()

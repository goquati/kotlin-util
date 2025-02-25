package io.github.goquati.kotlin.util.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.time.TimeSource.Monotonic.markNow


/**
 * usage:
 *
 * ```kotlin
 * class Foobar {
 *    companion object : QuatiLogger.Base()
 * }
 * ```
 *
 * Alternative: use an interface to avoid using an abstract class:
 *
 * ```kotlin
 * class Foobar {
 *    companion object : QuatiLogger by QuatiLogger.create(Foobar::class)
 * }
 * ```
 */
public interface QuatiLogger {
    public val log: Logger

    public abstract class Base : QuatiLogger {
        override val log: Logger = getLogger(javaClass.classForLogging)
    }

    public companion object {
        private val Class<*>.classForLogging
            get(): Class<*> = enclosingClass?.takeIf { it.kotlin.companionObject?.java == this } ?: this

        public fun create(clazz: KClass<*>): QuatiLogger = object : QuatiLogger {
            override val log: Logger = getLogger(clazz.java.classForLogging)
        }

        public fun create(name: String): QuatiLogger = object : QuatiLogger {
            override val log: Logger = getLogger(name)
        }
    }
}

public inline fun <reified T> Logger.executionTime(
    message: String,
    block: () -> T,
): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    this.info("start $message")
    val t0 = markNow()
    val result = block()
    val duration = markNow() - t0
    this.info("finished $message in $duration")
    return result
}

public inline fun <reified T> Logger.executionTimeCatching(
    message: String,
    errorMsg: (Throwable) -> String = { it.message ?: "<no-message>" },
    block: () -> T,
): Result<T> {
    contract {
        callsInPlace(errorMsg, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    this.info("start $message")
    val t0 = markNow()
    return try {
        val result = block()
        val duration = markNow() - t0
        this.info("finished $message in $duration")
        Result.success(result)
    } catch (t: Throwable) {
        val duration = markNow() - t0
        this.error("finished $message in $duration with error: ${errorMsg(t)}")
        Result.failure(t)
    }
}
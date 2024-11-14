package io.github.goquati.kotlin.util.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.TimeSource.Monotonic.markNow


public interface QuatiLogger {
    public val quatiLoggerName: String? get() = null
    public val quatiLoggerRemoveProxyClass: Boolean get() = true

    public val log: Logger get() {
        if (quatiLoggerName != null)
            return LoggerFactory.getLogger(quatiLoggerName)
        val clazz = this::class.java.let {
            if (quatiLoggerRemoveProxyClass && it.simpleName.contains("$$"))
                it.superclass
            else
                it
        }
        return LoggerFactory.getLogger(clazz)
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
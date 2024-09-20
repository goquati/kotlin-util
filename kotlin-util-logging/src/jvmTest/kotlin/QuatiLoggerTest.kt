import io.github.goquati.kotlin.util.logging.QuatiLogger
import io.github.goquati.kotlin.util.logging.executionTime
import io.github.goquati.kotlin.util.logging.executionTimeCatching
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private open class Foobar(removeProxyClass: Boolean) : QuatiLogger(removeProxyClass = removeProxyClass)
private class `Foobar$$Proxy$$0` : Foobar(true)
private class `FoobarWithProxy$$Proxy$$0` : Foobar(false)

class QuatiLoggerTest {
    @Test
    fun testQuatiLogger() {
        QuatiLogger().log.name shouldBe "io.github.goquati.kotlin.util.logging.QuatiLogger"
        QuatiLogger("Hello").log.name shouldBe "Hello"
        Foobar(true).log.name shouldBe "Foobar"
        Foobar(false).log.name shouldBe "Foobar"
        `Foobar$$Proxy$$0`().log.name shouldBe "Foobar"
        `FoobarWithProxy$$Proxy$$0`().log.name shouldBe "FoobarWithProxy\$\$Proxy\$\$0"
    }

    @Test
    fun testExecutionTime() {
        val log: Logger = mockk(relaxed = true)
        log.executionTime("hello") {
            runBlocking { delay(200) }
            47
        } shouldBe 47
        verifyOrder {
            log.info("start hello")
            log.info(match {
                val result = "^finished hello in (.*)$".toRegex().matchEntire(it) ?: return@match false
                val duration = Duration.parse(result.groupValues[1])
                duration shouldBeGreaterThanOrEqualTo 200.milliseconds
                duration shouldBeLessThan 2_000.milliseconds
                true
            })
        }
    }

    @Test
    fun testExecutionTimeCatchingSuccess() {
        val log: Logger = mockk(relaxed = true)
        log.executionTimeCatching("hello") {
            runBlocking { delay(200) }
            47
        }.getOrThrow() shouldBe 47
        verifyOrder {
            log.info("start hello")
            log.info(match {
                val result = "^finished hello in (.*)$".toRegex().matchEntire(it) ?: return@match false
                val duration = Duration.parse(result.groupValues[1])
                duration shouldBeGreaterThanOrEqualTo 200.milliseconds
                duration shouldBeLessThan 2_000.milliseconds
                true
            })
        }
    }

    @Test
    fun testExecutionTimeCatchingFailure() {
        val log: Logger = mockk(relaxed = true)
        log.executionTimeCatching<Int>("hello") {
            runBlocking { delay(200) }
            throw NotImplementedError("foobar")
        }.exceptionOrNull() shouldBe NotImplementedError("foobar")
        verifyOrder {
            log.info("start hello")
            log.error(match {
                val result =
                    "^finished hello in (.*) with error: foobar$".toRegex().matchEntire(it) ?: return@match false
                val duration = Duration.parse(result.groupValues[1])
                duration shouldBeGreaterThanOrEqualTo 200.milliseconds
                duration shouldBeLessThan 2_000.milliseconds
                true
            })
        }
    }

    @Test
    fun testExecutionTimeCatchingFailureWithHandler() {
        val log: Logger = mockk(relaxed = true)
        log.executionTimeCatching<Int>("hello", { "123 ${it.message}" }) {
            runBlocking { delay(200) }
            throw NotImplementedError("foobar")
        }.exceptionOrNull() shouldBe NotImplementedError("foobar")
        verifyOrder {
            log.info("start hello")
            log.error(match {
                val result = "^finished hello in (.*) with error: 123 foobar$".toRegex().matchEntire(it)
                    ?: return@match false
                val duration = Duration.parse(result.groupValues[1])
                duration shouldBeGreaterThanOrEqualTo 200.milliseconds
                duration shouldBeLessThan 2_000.milliseconds
                true
            })
        }
    }

    @Test
    fun testExecutionTimeCatchingFailureWithNoMsg() {
        val log: Logger = mockk(relaxed = true)
        log.executionTimeCatching<Int>("hello") {
            runBlocking { delay(200) }
            throw Exception()
        }.exceptionOrNull() shouldBe Exception()
        verifyOrder {
            log.info("start hello")
            log.error(match {
                val result = "^finished hello in (.*) with error: <no-message>$".toRegex().matchEntire(it)
                    ?: return@match false
                val duration = Duration.parse(result.groupValues[1])
                duration shouldBeGreaterThanOrEqualTo 200.milliseconds
                duration shouldBeLessThan 2_000.milliseconds
                true
            })
        }
    }
}
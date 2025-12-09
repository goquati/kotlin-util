import de.quati.kotlin.util.logging.QuatiLogger
import de.quati.kotlin.util.logging.executionTime
import de.quati.kotlin.util.logging.executionTimeCatching
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

class Foobar0 : QuatiLogger.Base() {
    class HelloWorld : QuatiLogger.Base()
}

class Foobar1 {
    companion object : QuatiLogger.Base()
}

class Foobar2 {
    companion object : QuatiLogger by QuatiLogger.create(Foobar2::class)
}

class Foobar3 {
    companion object : QuatiLogger by QuatiLogger.create("Hello")
}


class QuatiLoggerTest {
    @Test
    fun testQuatiLogger() {
        Foobar0().log.name shouldBe "Foobar0"
        Foobar0.HelloWorld().log.name shouldBe "Foobar0${'$'}HelloWorld"
        Foobar1.log.name shouldBe "Foobar1"
        Foobar2.log.name shouldBe "Foobar2"
        Foobar3.log.name shouldBe "Hello"
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
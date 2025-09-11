import io.github.goquati.kotlin.util.coroutine.lazyAsync
import io.github.goquati.kotlin.util.coroutine.withNullableTimeout
import io.github.goquati.kotlin.util.coroutine.withNullableTimeoutOrNull
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class JobUtilTest {
    @Test
    fun testLazyAsync(): TestResult = runTest {
        val calls = MutableStateFlow(0)
        val value = lazyAsync {
            calls.update { it + 1 }
            "foobar"
        }
        delay(200)
        calls.value shouldBe 0
        value.await() shouldBe "foobar"
        calls.value shouldBe 1
        value.await() shouldBe "foobar"
        delay(200)
        calls.value shouldBe 1
    }

    @Test
    fun testWithNullableTimeout(): TestResult = runTest {
        withNullableTimeout(timeout = null) { delay(50); 47 } shouldBe 47
        withNullableTimeout(timeout = 200.milliseconds) { delay(50); 47 } shouldBe 47
        shouldThrowExactly<TimeoutCancellationException> {
            withNullableTimeout(timeout = 50.milliseconds) { delay(200); 47 }
        }
    }

    @Test
    fun testWithNullableTimeoutOrNull(): TestResult = runTest {
        withNullableTimeoutOrNull(timeout = null) { delay(50); 47 } shouldBe 47
        withNullableTimeoutOrNull(timeout = 200.milliseconds) { delay(50); 47 } shouldBe 47
        withNullableTimeoutOrNull(timeout = 50.milliseconds) { delay(200); 47 } shouldBe null
    }
}
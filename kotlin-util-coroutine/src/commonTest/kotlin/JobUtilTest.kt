import io.github.goquati.kotlin.util.coroutine.lazyAsync
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

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
}
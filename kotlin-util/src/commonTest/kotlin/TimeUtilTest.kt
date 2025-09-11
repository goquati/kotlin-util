import io.github.goquati.kotlin.util.WithTimestamp
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TimeUtilTest {
    @Test
    fun testWithTimestamp() {
        @OptIn(ExperimentalTime::class)
        val t0 = Clock.System.now()
        WithTimestamp(47).apply {
            value shouldBe 47
            @OptIn(ExperimentalTime::class)
            timestamp shouldBeGreaterThanOrEqualTo t0
        }
        @OptIn(ExperimentalTime::class)
        WithTimestamp(47, t0).apply {
            value shouldBe 47
            @OptIn(ExperimentalTime::class)
            timestamp shouldBe t0
        }
    }
}
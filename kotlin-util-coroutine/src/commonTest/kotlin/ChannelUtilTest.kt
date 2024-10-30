import io.github.goquati.kotlin.util.coroutine.toClosedChannel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ChannelUtilTest {
    @Test
    fun testToClosedChannel(): TestResult = runTest {
        listOf(1,2,3,4).toClosedChannel().toList() shouldBe listOf(1,2,3,4)
        sequenceOf(1, 2, 3, 4).asIterable().toClosedChannel().toList() shouldBe listOf(1,2,3,4)
    }
}
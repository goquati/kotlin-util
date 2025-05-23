import io.github.goquati.kotlin.util.coroutine.sendAll
import io.github.goquati.kotlin.util.coroutine.toBatchedFlow
import io.github.goquati.kotlin.util.coroutine.toReceiveChannel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ChannelUtilTest {
    @Test
    fun testSendAll(): TestResult = runTest {
        channelFlow { listOf(1, 2, 3).sendAll() }.toList() shouldBe listOf(1, 2, 3)
        channelFlow { flowOf(1, 2, 3).sendAll() }.toList() shouldBe listOf(1, 2, 3)
    }

    @Test
    fun testToReceiveChannel(): TestResult = runTest {
        listOf(1, 2, 3, 4).toReceiveChannel().toList() shouldBe listOf(1, 2, 3, 4)
        sequenceOf(1, 2, 3, 4).asIterable().toReceiveChannel().toList() shouldBe listOf(1, 2, 3, 4)
    }

    @Test
    fun testToBatchedFlow(): TestResult = runTest {
        listOf<Int>().toReceiveChannel().toBatchedFlow().firstOrNull() shouldBe null
        listOf(1, 2, 3, 4).toReceiveChannel().toBatchedFlow().single() shouldBe listOf(1, 2, 3, 4)
        listOf(1, 2, 3).toReceiveChannel().toBatchedFlow(maxBatchSize = 3).single() shouldBe listOf(1, 2, 3)
        listOf(1, 2, 3, 4).toReceiveChannel().toBatchedFlow(maxBatchSize = 3).toList() shouldBe
                listOf(listOf(1, 2, 3), listOf(4))

        val channel = Channel<Int>(Channel.UNLIMITED)
        channel.send(1)
        channel.toBatchedFlow(maxBatchSize = 3).withIndex().collect { (idx, value) ->
            when (idx) {
                0 -> {
                    value shouldBe listOf(1)
                    repeat(3) { channel.send(it) }
                }

                1 -> {
                    value shouldBe listOf(0, 1, 2)
                    repeat(1) { channel.send(it) }
                }

                2 -> {
                    value shouldBe listOf(0)
                    repeat(4) { channel.send(it) }
                }

                3 -> {
                    value shouldBe listOf(0, 1, 2)
                }

                4 -> {
                    value shouldBe listOf(3)
                    repeat(1) { channel.send(it) }
                }

                5 -> {
                    value shouldBe listOf(0)
                    channel.send(7)
                    channel.close()
                }

                6 -> {
                    value shouldBe listOf(7)
                }

                else -> throw Exception("Unexpected error")
            }
        }
    }
}
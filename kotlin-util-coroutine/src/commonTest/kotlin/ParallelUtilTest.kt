import io.github.goquati.kotlin.coroutine.util.awaitAll
import io.github.goquati.kotlin.coroutine.util.mapParallel
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test


class ParallelUtilTest {
    @Test
    fun testMapParallel(): TestResult = runTest {
        flowOf(1, 2, 3, 4).mapParallel { delay(0); "$it" }
            .toList() shouldContainExactlyInAnyOrder listOf("1", "2", "3", "4")
        flowOf(1, 2, 3, 4).mapParallel(concurrency = 2) { delay(0); "$it" }
            .toList() shouldContainExactlyInAnyOrder listOf("1", "2", "3", "4")
    }

    @Test
    fun testAwaitAll(): TestResult = runTest {
        awaitAll(
            { delay(0); 47 },
            { delay(0); "foobar" },
        ) shouldBe Pair(47, "foobar")
        awaitAll(
            { delay(0); 47 },
            { delay(0); "foobar" },
            { delay(0); true },
        ) shouldBe Triple(47, "foobar", true)
    }
}
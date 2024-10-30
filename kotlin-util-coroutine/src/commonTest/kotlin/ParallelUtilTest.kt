import io.github.goquati.kotlin.util.coroutine.awaitAll
import io.github.goquati.kotlin.util.coroutine.launchJobs
import io.github.goquati.kotlin.util.coroutine.launchWorker
import io.github.goquati.kotlin.util.coroutine.mapParallel
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
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
    fun testLaunchWorker(): TestResult = runTest {
        val state = MutableStateFlow(0)
        launchWorker(nofWorkers = 30) {
            state.update { it + 1 }
        }.joinAll()
        state.value shouldBe 30
    }

    @Test
    fun testLaunchJobs(): TestResult = runTest {
        launchJobs(nofWorkers = 30) {
            it
        }.awaitAll() shouldContainExactlyInAnyOrder (0..<30).toList()
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
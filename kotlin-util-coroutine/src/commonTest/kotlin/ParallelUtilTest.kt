import io.github.goquati.kotlin.util.coroutine.CoalescingTaskRunner
import io.github.goquati.kotlin.util.coroutine.CoalescingTaskRunnerWithResult
import io.github.goquati.kotlin.util.coroutine.ConcurrentQuatiMap
import io.github.goquati.kotlin.util.coroutine.ConcurrentQuatiMapSimple
import io.github.goquati.kotlin.util.coroutine.IConcurrentQuatiMap
import io.github.goquati.kotlin.util.coroutine.awaitAll
import io.github.goquati.kotlin.util.coroutine.launchJobs
import io.github.goquati.kotlin.util.coroutine.launchWorker
import io.github.goquati.kotlin.util.coroutine.mapParallel
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.test.Test


class ParallelUtilTest {
    @Test
    fun testConcurrentQuatiMap(): TestResult = runTest {
        suspend fun test(data: IConcurrentQuatiMap<Int, String>) {
            val s0 = data.getCurrentKeys()
            data.getOrPut(1) { "1" } shouldBe "1"
            val s1 = data.getCurrentKeys()
            data.getOrPut(1) { "2" } shouldBe "1"
            val s2 = data.getCurrentKeys()
            data.getOrPut(2) { "2" } shouldBe "2"
            val s3 = data.getCurrentKeys()
            s0 shouldBe setOf()
            s1 shouldBe setOf(1)
            s2 shouldBe setOf(1)
            s3 shouldBe setOf(1, 2)
        }
        test(ConcurrentQuatiMap())
        test(ConcurrentQuatiMapSimple())
    }

    @Test
    fun testMapParallel(): TestResult = runTest {
        flowOf(1, 2, 3, 4).mapParallel { yield(); "$it" }
            .toList() shouldContainExactlyInAnyOrder listOf("1", "2", "3", "4")
        flowOf(1, 2, 3, 4).mapParallel(concurrency = 2) { yield(); "$it" }
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
            { yield(); 47 },
            { yield(); "foobar" },
        ) shouldBe Pair(47, "foobar")
        awaitAll(
            { yield(); 47 },
            { yield(); "foobar" },
            { yield(); true },
        ) shouldBe Triple(47, "foobar", true)
    }

    @Test
    fun testCoalescingTaskRunner(): TestResult = runTest {
        withContext(Dispatchers.Default) { // delays are skipped in test dispatcher
            var count = 0
            CoalescingTaskRunner {
                count++
                delay(100)
            }.use { runner ->
                repeat(10) {
                    runner.schedule()
                    delay(3)
                }
                delay(400)
                count shouldBe 2
                runner.schedule()
            }
            count shouldBe 3
        }
    }

    private object TestException : Exception()

    @Test
    fun testCoalescingTaskRunnerErrorHandler(): TestResult = runTest {
        withContext(Dispatchers.Default) { // delays are skipped in test dispatcher
            var count = 0
            CoalescingTaskRunner(
                errorHandler = {
                    count++
                    it shouldBe TestException
                },
            ) {
                delay(100)
                throw TestException
            }.use { runner ->
                repeat(10) {
                    runner.schedule()
                    delay(3)
                }
                delay(400)
                @OptIn(ExperimentalCoroutinesApi::class)
                advanceUntilIdle()
                count shouldBe 2
                runner.schedule()
            }
            count shouldBe 3
        }
    }


    @Test
    fun testCoalescingTaskRunnerWithResult(): TestResult = runTest {
        withContext(Dispatchers.Default) { // delays are skipped in test dispatcher
            var count = 0
            val result = CoalescingTaskRunnerWithResult {
                delay(100)
                count++
            }.use { runner ->
                val results = (0..<10).map {
                    delay(3)
                    async {
                        runner.schedule()
                    }
                }.awaitAll()
                results.toSet() shouldBe setOf(0, 1)
                count shouldBe 2
                runner.schedule()
            }
            result shouldBe 2
            count shouldBe 3
        }
    }
}

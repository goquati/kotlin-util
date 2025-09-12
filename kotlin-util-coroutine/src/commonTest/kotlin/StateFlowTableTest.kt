import io.github.goquati.kotlin.util.coroutine.StateEvent
import io.github.goquati.kotlin.util.coroutine.StateFlowTable
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class StateFlowTableTest {

    @Test
    fun `set and get should work`(): TestResult = runTest {
        val table = StateFlowTable<Long, String, Int>()
        table.set(1L, "session1", 42)
        val state = table.get(1L).value
        state["session1"]?.value shouldBe 42
    }

    @Test
    fun `drop removes entry`(): TestResult = runTest {
        val table = StateFlowTable<Long, String, Int>()
        table.set(1L, "session1", 100)
        table.drop(1L, "session1")
        val state = table.get(1L).value
        state.size shouldBe 0
    }

    @Test
    fun `update modifies entry`(): TestResult = runTest {
        val table = StateFlowTable<Long, String, Int>()
        table.set(1L, "session1", 10)
        table.update(1L, "session1") { _, old -> (old ?: 0) + 5 }
        val state = table.get(1L).value
        state["session1"]?.value shouldBe 15
    }

    @Test
    fun `dropAllBefore removes older timestamps`(): TestResult = runTest {
        @OptIn(ExperimentalTime::class)
        val testClock = object : Clock {
            var current = Instant.fromEpochMilliseconds(0)
            fun plus(d: Duration) {
                current = current.plus(d)
            }
            override fun now() = current
        }
        @OptIn(ExperimentalTime::class)
        val table = StateFlowTable<Long, String, Int>(clock = testClock)
        table.update(1L, "old") { _, _ -> 47 }
        testClock.plus(1.seconds)
        @OptIn(ExperimentalTime::class) val tm = testClock.now()
        testClock.plus(1.seconds)
        table.update(1L, "new") { _, _ -> 48 }

        @OptIn(ExperimentalTime::class) table.dropAllBefore(tm.minus(10.seconds))
        table.get(1L).value.also { state ->
            state.size shouldBe 2
            state["old"]?.value shouldBe 47
            state["new"]?.value shouldBe 48
        }
        @OptIn(ExperimentalTime::class) table.dropAllBefore(tm)
        table.get(1L).value.also { state ->
            state.size shouldBe 1
            state["new"]?.value shouldBe 48
        }
    }

    @Test
    fun `subscribe emits create update delete`(): TestResult = runTest {
        val table = StateFlowTable<Long, String, Int>()
        val events = mutableListOf<StateEvent<String, Int>>()
        val ready = Channel<Unit>(1)

        val job = launch {
            table.subscribe(1L).onStart {
                ready.send(Unit) // signal subscription started
            }.take(4).toList(events)
        }
        ready.receive() // wait until the collector is active

        table.set(1L, "session1", 10)
        yield()
        table.set(1L, "session1", 20)
        yield()
        table.set(1L, "session1", 20)
        yield()
        table.set(1L, "session10", 100)
        yield()
        table.drop(1L, "session1")
        yield()
        job.join()

        val (e1, e2, e3, e4) = events
        e1 shouldBe StateEvent.Set(key = "session1", old = null, new = 10)
        e2 shouldBe StateEvent.Set(key = "session1", old = 10, new = 20)
        e3 shouldBe StateEvent.Set(key = "session10", old = null, new = 100)
        e4 shouldBe StateEvent.Unset(key = "session1", old = 20)
    }
}
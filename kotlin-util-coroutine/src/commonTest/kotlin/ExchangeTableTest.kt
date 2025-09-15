import io.github.goquati.kotlin.util.QuatiException
import io.github.goquati.kotlin.util.coroutine.ExchangeTable
import io.github.goquati.kotlin.util.coroutine.StateEvent
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class ExchangeTableTest {
    @Test
    fun `waitFor completes when notify is called`(): TestResult = runTest {
        val table = ExchangeTable<String, String, String, String>()
        val deferred = async {
            table.waitFor("outer", "inner", data = "request")
        }
        table.notify("outer", "inner", "response")
        val result = deferred.await()
        result shouldBe "response"
    }

    @Test
    fun `waitFor throws GatewayTimeout when response never arrives`(): TestResult = runTest {
        val table = ExchangeTable<String, String, String, String>()
        shouldThrowExactly<QuatiException.GatewayTimeout> {
            table.waitFor("outer", "inner", data = "req", timeout = 100.milliseconds)
        }
    }

    @Test
    fun `second waitFor on same id fails with InternalServerError`(): TestResult = runTest {
        val table = ExchangeTable<String, String, String, String>()
        val job1 = async {
            table.waitFor("outer", "inner", data = "req1")
        }
        delay(100)
        shouldThrowExactly<QuatiException.InternalServerError> {
            table.waitFor("outer", "inner", data = "req2")
        }
        table.notify("outer", "inner", "resp1")
        job1.await() shouldBe "resp1"
    }

    @Test
    fun `notify without waiter does not crash`(): TestResult = runTest {
        val table = ExchangeTable<String, String, String, String>()
        shouldThrowExactly<QuatiException.GatewayTimeout> {
            table.waitFor("outer", "inner", data = "req", timeout = 100.milliseconds)
        }
    }

    @Test
    fun `subscribeSend receives create and delete events`(): TestResult = runTest {
        val table = ExchangeTable<String, String, String, String>()
        val events = mutableListOf<StateEvent<String, String>>()

        val job = async {
            table.subscribeSend("outer").collect {
                events.add(it)
            }
        }
        delay(100)

        // one waiter inserts a request
        val waiter = async {
            table.waitFor("outer", "inner", data = "request")
        }
        delay(100)

        // responder answers
        table.notify("outer", "inner", "response")

        waiter.await()
        delay(100) // allow flow to emit

        job.cancel() // stop collecting

        val create = events.filterIsInstance<StateEvent.Set<String, String>>()
        val delete = events.filterIsInstance<StateEvent.Unset<String, String>>()

        create.size shouldBe 1
        create.first().new shouldBe "request"

        delete.size shouldBe 1
        delete.first().old shouldBe "request"
    }

    @Test
    fun `dropAllBefore removes older timestamps`(): TestResult = runTest {
        @OptIn(ExperimentalTime::class)
        val testClock = TestClock()
        @OptIn(ExperimentalTime::class)
        val table = ExchangeTable<String, String, String, String>(clock = testClock)
        @OptIn(ExperimentalTime::class)
        table.dropAllBefore(testClock.now())
    }
}

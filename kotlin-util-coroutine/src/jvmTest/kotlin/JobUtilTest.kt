import io.github.goquati.kotlin.util.coroutine.loopCatching
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

data object JobFailedException : Exception() {
    private fun readResolve(): Any = JobFailedException
}

class JobUtilTest {

    @Test
    fun testLoopCatching(): Unit = runBlocking {
        var success = 0
        var fails = 0

        val job = loopCatching({
            if (it is CancellationException) throw it
            it shouldBe JobFailedException
            fails++
        }) {
            if (fails < success) throw JobFailedException else success++
            delay(50)
        }
        delay(800)
        job.cancelAndJoin()

        success shouldBeGreaterThan 0
        fails shouldBeGreaterThan 0
    }
}
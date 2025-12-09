import de.quati.kotlin.util.SingleResult
import de.quati.kotlin.util.coroutine.singleResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SingleResultUtilTest {
    @Test
    fun testSingleResult(): TestResult = runTest {
        flowOf<Int>().singleResult() shouldBe SingleResult.None
        flowOf(1).singleResult() shouldBe SingleResult.Success(1)
        flow {
            emit(1)
            emit(2)
            error("should not be called")
        }.singleResult() shouldBe SingleResult.TooMany
    }
}
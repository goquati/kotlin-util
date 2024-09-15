import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.coroutine.util.filterFailure
import io.github.goquati.kotlin.coroutine.util.filterSuccess
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import io.github.goquati.kotlin.coroutine.util.toResultList
import io.github.goquati.kotlin.coroutine.util.toResultSet
import kotlinx.coroutines.flow.toList

class ResultUtilTest {

    @Test
    fun testFilter(): TestResult = runTest {
        val data = flowOf(
            Failure(1),
            Success("2"),
            Failure(3),
            Success("4"),
            Failure(5),
            Success("6"),
        )
        data.filterSuccess().toList() shouldBe listOf("2", "4", "6")
        data.filterFailure().toList() shouldBe listOf(1, 3, 5)
    }

    @Test
    fun testToCollection(): TestResult = runTest {
        val data1 = flowOf(
            Success("1"),
            Failure(2),
            Success("3"),
        )
        val data2 = flowOf(
            Success("1"),
            Success("2"),
            Success("3"),
        )
        data1.toResultList().failure shouldBe 2
        data2.toResultList().success shouldBe listOf("1", "2", "3")

        data1.toResultSet().failure shouldBe 2
        data2.toResultSet().success shouldBe setOf("1", "2", "3")
    }
}
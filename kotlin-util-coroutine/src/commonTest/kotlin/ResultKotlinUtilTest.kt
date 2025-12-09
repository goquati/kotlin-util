import de.quati.kotlin.util.coroutine.*
import de.quati.kotlin.util.exceptionOrThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlinx.coroutines.flow.toList

class ResultKotlinUtilTest {
    data class Error(val msg: Int) : Throwable()

    @Test
    fun testFilter(): TestResult = runTest {
        val data = flowOf(
            Result.failure(Error(1)),
            Result.success("2"),
            Result.failure(Error(3)),
            Result.success("4"),
            Result.failure(Error(5)),
            Result.success("6"),
        )
        data.filterSuccess().toList() shouldBe listOf("2", "4", "6")
        data.filterFailure().toList() shouldBe listOf(Error(1), Error(3), Error(5))
    }

    @Test
    fun testOnEachFailure(): TestResult = runTest {
        val data = flowOf(
            Result.failure(Error(1)),
            Result.success("2"),
            Result.failure(Error(3)),
            Result.success("4"),
            Result.failure(Error(5)),
            Result.success("6"),
        )
        val errors = mutableListOf<Error>()
        data.onEachFailure { errors.add(it as Error) }.toList() shouldBe data.toList()
        errors shouldBe listOf(Error(1), Error(3), Error(5))
    }

    @Test
    fun testToCollection(): TestResult = runTest {
        val data1 = flowOf(
            Result.success("1"),
            Result.failure(Error(2)),
            Result.success("3"),
        )
        val data2 = flowOf(
            Result.success("1"),
            Result.success("2"),
            Result.success("3"),
        )
        data1.toResultList().exceptionOrThrow() shouldBe Error(2)
        data2.toResultList().getOrThrow() shouldBe listOf("1", "2", "3")

        data1.toResultSet().exceptionOrThrow() shouldBe Error(2)
        data2.toResultSet().getOrThrow() shouldBe setOf("1", "2", "3")
    }
}
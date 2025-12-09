import de.quati.kotlin.util.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.Result

class ResultKotlinUtilTest {
    data class Error(val msg: String) : Throwable()
    companion object {
        val r1: Result<String> = Result.failure(Error("r1"))
        val r2: Result<String> = Result.success("r2")
    }

    @Test
    fun testGetOr() {
        r1.getOrDefault("foobar1") shouldBe "foobar1"
        r2.getOrDefault("foobar2") shouldBe "r2"

        r1.getOrDefault("foobar1") shouldBe "foobar1"
        r2.getOrDefault("foobar2") shouldBe "r2"
    }

    @Test
    fun testGetFailureOr() {
        r1.exceptionOrNull() shouldBe Error("r1")
        r2.exceptionOrNull() shouldBe null

        r1.exceptionOrThrow() shouldBe Error("r1")
        shouldThrowExactly<IllegalStateException> {
            r2.exceptionOrThrow() shouldBe null
        }
    }

    @Test
    fun testGetOrThrow() {
        shouldThrowExactly<Error> { r1.getOrThrow() }
        r2.getOrThrow() shouldBe "r2"
    }

    @Test
    fun testMap() {
        r1.map { 1 }.exceptionOrNull() shouldBe Error("r1")
        r2.map { 2 }.getOrThrow() shouldBe 2
    }

    @Test
    fun testFlatMap() {
        r1.flatMap { Result.failure<String>(Error("r1.2")) }.exceptionOrThrow() shouldBe Error("r1")
        r1.flatMap { Result.success(1) }.exceptionOrThrow() shouldBe Error("r1")
        r2.flatMap { Result.failure<String>(Error("r2.2")) }.exceptionOrThrow() shouldBe Error("r2.2")
        r2.flatMap { Result.success(2) }.getOrThrow() shouldBe 2
    }

    @Test
    fun testMapError() {
        r1.mapError { 'r' }.failureOrNull shouldBe 'r'
        r2.mapError { 'r' }.successOrNull shouldBe "r2"

        Result.success('r').mapError { 47 }.successOrNull shouldBe 'r'
        Result.failure<NotImplementedError>(NotImplementedError("bar"))
            .mapError { it.message }.failureOrNull shouldBe "bar"
    }

    @Test
    fun testFlatten() {
        Result.success(r1).flatten().exceptionOrThrow() shouldBe Error("r1")
        Result.success(r2).flatten().getOrThrow() shouldBe "r2"
        @Suppress("UNCHECKED_CAST")
        (Result.failure<String>(Error("r1")) as Result<Result<String>>).flatten()
            .exceptionOrThrow() shouldBe Error("r1")
    }

    @Test
    fun testFilter() {
        val data = listOf(
            Result.failure(Error("1")),
            Result.success("2"),
            Result.failure(Error("3")),
            Result.success("4"),
            Result.failure(Error("5")),
            Result.success("6"),
        )
        data.filterSuccess() shouldBe listOf("2", "4", "6")
        data.filterFailure() shouldBe listOf(Error("1"), Error("3"), Error("5"))
    }

    @Test
    fun testOnEachFailure() {
        val data = listOf(
            Result.failure(Error("1")),
            Result.success("2"),
            Result.failure(Error("3")),
            Result.success("4"),
            Result.failure(Error("5")),
            Result.success("6"),
        )
        val errors = mutableListOf<Error>()
        data.onEachFailure { errors.add(it as Error) } shouldBe data
        errors shouldBe listOf(Error("1"), Error("3"), Error("5"))
    }

    @Test
    fun testToCollection() {
        val data1 = listOf(
            Result.success("1"),
            Result.failure(Error("2")),
            Result.success("3"),
        )
        val data2 = listOf(
            Result.success("1"),
            Result.success("2"),
            Result.success("3"),
        )
        data1.toResultList().exceptionOrThrow() shouldBe Error("2")
        data2.toResultList().getOrThrow() shouldBe listOf("1", "2", "3")

        data1.toResultSet().exceptionOrThrow() shouldBe Error("2")
        data2.toResultSet().getOrThrow() shouldBe setOf("1", "2", "3")
    }
}
import io.github.goquati.kotlin.util.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ResultUtilTest {
    data class Error(val msg: String): Throwable()
    companion object {
        val r1: Result<String, Error> = Failure(Error("r1"))
        val r2: Result<String, Error> = Success("r2")
    }

    @Test
    fun testGetOr() {
        r1.getOr("foobar1") shouldBe "foobar1"
        r2.getOr("foobar2") shouldBe "r2"

        r1.getOr { "foobar1" } shouldBe "foobar1"
        r2.getOr { "foobar2" } shouldBe "r2"
    }

    @Test
    fun testGetFailureOr() {
        r1.getFailureOr(Error("foobar1")) shouldBe Error("r1")
        r2.getFailureOr(Error("foobar2")) shouldBe Error("foobar2")

        r1.getFailureOr { Error("foobar1") } shouldBe Error("r1")
        r2.getFailureOr { Error("foobar2") } shouldBe Error("foobar2")
    }

    @Test
    fun testGetOrThrow() {
        shouldThrowExactly<Error> { r1.getOrThrow() }
        r2.getOrThrow() shouldBe "r2"
    }

    @Test
    fun testToKotlin() {
        r1.toKotlin() shouldBe kotlin.Result.failure(Error("r1"))
        r2.toKotlin() shouldBe kotlin.Result.success("r2")

        listOf(r1, r2).toKotlin() shouldBe listOf(
            kotlin.Result.failure(Error("r1")),
            kotlin.Result.success("r2"),
        )
    }

    @Test
    fun testMap() {
        r1.map { 1 }.failureOrNull shouldBe Error("r1")
        r2.map { 2 }.successOrNull shouldBe 2
    }

    @Test
    fun testFlatMap() {
        r1.flatMap { Failure(Error("r1.2")) }.failureOrNull shouldBe Error("r1")
        r1.flatMap { Success(1) }.failureOrNull shouldBe Error("r1")
        r2.flatMap { Failure(Error("r2.2")) }.failureOrNull shouldBe Error("r2.2")
        r2.flatMap { Success(2) }.successOrNull shouldBe 2
    }

    @Test
    fun testFlatMapFailure() {
        r1.flatMapFailure { Failure(Error("r1.2")) }.failureOrNull shouldBe Error("r1.2")
        r1.flatMapFailure { Success("1") }.successOrNull shouldBe "1"
        r2.flatMapFailure { Failure(Error("r2.2")) }.successOrNull shouldBe "r2"
        r2.flatMapFailure { Success("2") }.successOrNull shouldBe "r2"
    }

    @Test
    fun testMapError() {
        r1.mapError { 'r' }.failureOrNull shouldBe 'r'
        r2.mapError { 'r' }.successOrNull shouldBe "r2"

        kotlin.Result.success('r').mapError { 47 }.successOrNull shouldBe 'r'
        kotlin.Result.failure<NotImplementedError>(NotImplementedError("bar"))
            .mapError { it.message }.failureOrNull shouldBe "bar"
    }

    @Test
    fun testFlatten() {
        Success(r1).flatten().failureOrNull shouldBe Error("r1")
        Success(r2).flatten().successOrNull shouldBe "r2"
        (Failure(Error("r1")) as Result<Result<String, Error>, Error>).flatten().failureOrNull shouldBe Error("r1")
    }

    @Test
    fun testFilter() {
        val data = listOf(
            Failure(1),
            Success("2"),
            Failure(3),
            Success("4"),
            Failure(5),
            Success("6"),
        )
        data.filterSuccess() shouldBe listOf("2", "4", "6")
        data.filterFailure() shouldBe listOf(1, 3, 5)
    }

    @Test
    fun testOnEachFailure() {
        val data = listOf(
            Failure(1),
            Success("2"),
            Failure(3),
            Success("4"),
            Failure(5),
            Success("6"),
        )
        val errors = mutableListOf<Int>()
        data.onEachFailure { errors.add(it) } shouldBe data
        errors shouldBe listOf(1, 3, 5)
    }

    @Test
    fun testToCollection() {
        val data1 = listOf(
            Success("1"),
            Failure(2),
            Success("3"),
        )
        val data2 = listOf(
            Success("1"),
            Success("2"),
            Success("3"),
        )
        data1.toResultList().failureOrNull shouldBe 2
        data2.toResultList().successOrNull shouldBe listOf("1", "2", "3")

        data1.toResultSet().failureOrNull shouldBe 2
        data2.toResultSet().successOrNull shouldBe setOf("1", "2", "3")
    }
}
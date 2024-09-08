import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Success
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ResultTest {
    data class Error(val msg: String)
    companion object {
        val r1: Result<String, Error> = Failure(Error("r1"))
        val r2: Result<String, Error> = Success("r2")
    }

    @Test
    fun testBasic() {
        r1.isFailure shouldBe true
        r2.isFailure shouldBe false

        r1.isSuccess shouldBe false
        r2.isSuccess shouldBe true

        r1.failureOrNull shouldBe Error("r1")
        r2.failureOrNull shouldBe null

        r1.successOrNull shouldBe null
        r2.successOrNull shouldBe "r2"

        r1.asFailure shouldBe r1
        shouldThrowExactly<IllegalStateException> { r2.asFailure }

        shouldThrowExactly<IllegalStateException> { r1.asSuccess }
        r2.asSuccess shouldBe r2

        r1.toString() shouldBe "Failure(Error(msg=r1))"
        r2.toString() shouldBe "Success(r2)"
    }
}
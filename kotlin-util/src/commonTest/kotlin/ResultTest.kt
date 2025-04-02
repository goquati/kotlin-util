import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.util.isFailure
import io.github.goquati.kotlin.util.isSuccess
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
        Success("hello").successOrNull shouldBe  "hello"
        Failure(123).failure shouldBe 123

        r1.isFailure() shouldBe true
        r2.isFailure() shouldBe false

        r1.isSuccess() shouldBe false
        r2.isSuccess() shouldBe true

        r1.failureOrNull shouldBe Error("r1")
        r2.failureOrNull shouldBe null

        r1.successOrNull shouldBe null
        r2.successOrNull shouldBe "r2"

        r1.toString() shouldBe "Failure(Error(msg=r1))"
        (r1 as Failure).toString() shouldBe "Failure(Error(msg=r1))"

        r2.toString() shouldBe "Success(r2)"
        (r2 as Success).toString() shouldBe "Success(r2)"
    }

    @Test
    fun testSmartCast() {
        if (r1.isSuccess()) error("error")
        else r1.failure shouldBe Error("r1")

        if (r1.isFailure()) r1.failure shouldBe Error("r1")
        else error("error")

        if (r2.isSuccess()) r2.value shouldBe "r2"
        else error("error")

        if (r2.isFailure()) error("error")
        else r2.value shouldBe "r2"
    }
}
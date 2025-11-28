import io.github.goquati.kotlin.util.Option
import io.github.goquati.kotlin.util.isSome
import io.github.goquati.kotlin.util.isUndefined
import io.github.goquati.kotlin.util.map
import io.github.goquati.kotlin.util.takeSome
import io.github.goquati.kotlin.util.toOption
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class OptionTest {
    @Test
    fun optionUndefinedTests() {
        Option.Undefined.apply {
            isUndefined shouldBe true
            isSome shouldBe false
            takeSome() shouldBe null
        }
    }

    @Test
    fun optionSomeTests() {
        Option.Some("hello").apply {
            isUndefined shouldBe false
            isSome shouldBe true
            takeSome()!!.value shouldBe "hello"
        }
        Option.Some<String?>(null).apply {
            isUndefined shouldBe false
            isSome shouldBe true
            takeSome()?.value shouldBe null
        }
    }

    @Test
    fun optionMapTests() {
        Option.Some("foobar").map { it.length }.takeSome()!!.value shouldBe 6
        (Option.Undefined as Option<String>).map { it.length } shouldBe Option.Undefined
    }

    @Test
    fun optionToOptionTests() {
        "foobar".toOption() shouldBe Option.Some("foobar")
        null.toOption<String?>() shouldBe Option.Some<String?>(null)
    }
}
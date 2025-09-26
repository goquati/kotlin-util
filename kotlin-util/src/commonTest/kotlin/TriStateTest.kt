import io.github.goquati.kotlin.util.TriState
import io.github.goquati.kotlin.util.getOr
import io.github.goquati.kotlin.util.getOrNull
import io.github.goquati.kotlin.util.isNull
import io.github.goquati.kotlin.util.isPresent
import io.github.goquati.kotlin.util.isUndefined
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class TriStateTest {
    @Test
    fun triStateNullTests() {
        TriState.Null.apply {
            this shouldBe TriState.of(null)
            isNull shouldBe true
            isUndefined shouldBe false
            isPresent shouldBe false
            getOrNull() shouldBe null
            getOr { "foobar" } shouldBe "foobar"
            getOr("foobar") shouldBe "foobar"
        }
    }

    @Test
    fun triStateUndefinedTests() {
        TriState.Undefined.apply {
            this shouldNotBe TriState.of(null)
            isNull shouldBe false
            isUndefined shouldBe true
            isPresent shouldBe false
            getOrNull() shouldBe null
            getOr { "foobar" } shouldBe "foobar"
            getOr("foobar") shouldBe "foobar"
        }
    }

    @Test
    fun triStateValueTests() {
        TriState.Value("hello").apply {
            this shouldBe TriState.of("hello")
            isNull shouldBe false
            isUndefined shouldBe false
            isPresent shouldBe true
            getOrNull() shouldBe "hello"
            getOr { "foobar" } shouldBe "hello"
            getOr("foobar") shouldBe "hello"
        }
    }
}
import de.quati.kotlin.util.ensurePrefix
import de.quati.kotlin.util.ensureSuffix
import de.quati.kotlin.util.takeIfNotBlank
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class StringTest {
    @Test
    fun testTakeIfNotBlank() {
        " ".takeIfNotBlank() shouldBe null
        "\n".takeIfNotBlank() shouldBe null
        "\t".takeIfNotBlank() shouldBe null
        "hello".takeIfNotBlank() shouldBe "hello"
    }

    @Test
    fun testEnsurePrefix() {
        "foobar".ensurePrefix("foo") shouldBe "foobar"
        "foobar".ensurePrefix("hello ") shouldBe "hello foobar"
    }

    @Test
    fun testEnsureSuffix() {
        "foobar".ensureSuffix("bar") shouldBe "foobar"
        "foobar".ensureSuffix("123") shouldBe "foobar123"
    }
}
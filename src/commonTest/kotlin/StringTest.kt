import io.github.goquati.kotlin.util.takeIfNotBlank
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
}
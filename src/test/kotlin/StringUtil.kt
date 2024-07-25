import io.github.klahap.kotlin.util.takeIfNotBlank
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class StringUtil {
    @Test
    fun `test takeIfNotBlank`() {
        " ".takeIfNotBlank() shouldBe null
        "\n".takeIfNotBlank() shouldBe null
        "\t".takeIfNotBlank() shouldBe null
        "hello".takeIfNotBlank() shouldBe "hello"
    }
}
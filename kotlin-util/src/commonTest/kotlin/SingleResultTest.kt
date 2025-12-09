import de.quati.kotlin.util.QuatiException
import de.quati.kotlin.util.SingleResult
import de.quati.kotlin.util.getOrNull
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SingleResultTest {

    @Test
    fun testSingleResultGetOrThrow() {
        shouldThrowExactly<QuatiException.NotFound> {
            SingleResult.None.getOrThrow()
        }.msg shouldBe "nothing found"
        SingleResult.Success("hello").getOrThrow() shouldBe "hello"
        shouldThrowExactly<QuatiException.Conflict> {
            SingleResult.TooMany.getOrThrow()
        }.msg shouldBe "multiple matches found"
        shouldThrowExactly<QuatiException.NotFound> {
            SingleResult.None.getOrThrow("foobar")
        }.msg shouldBe "foobar - nothing found"
        SingleResult.Success("hello").getOrThrow("foobar") shouldBe "hello"
        shouldThrowExactly<QuatiException.Conflict> {
            SingleResult.TooMany.getOrThrow("foobar")
        }.msg shouldBe "foobar - multiple matches found"
    }
}
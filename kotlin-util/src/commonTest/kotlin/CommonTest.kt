import de.quati.kotlin.util.asOrNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CommonTest {
    @Test
    fun testAsOrNull() {
        "foo".asOrNull<String>() shouldBe "foo"
        "foo".asOrNull<Int>() shouldBe null
    }
}
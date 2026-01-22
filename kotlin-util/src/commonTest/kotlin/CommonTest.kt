import de.quati.kotlin.util.asOrNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CommonTest {
    @Test
    fun testAsOrNull() {
        val d1: Any = "foo"

        d1.asOrNull<String>()!! shouldBe "foo"
        d1.length shouldBe 3 // check smart cass

        val d2: Any = "bar"
        d2.asOrNull<Int>() shouldBe null
    }
}
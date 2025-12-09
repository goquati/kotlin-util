import de.quati.kotlin.util.SingleResult
import de.quati.kotlin.util.getOrNull
import de.quati.kotlin.util.singleResult
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SingleResultUtilTest {

    @Test
    fun testSingleResultGetOrNull() {
        SingleResult.None.getOrNull() shouldBe null
        SingleResult.Success("hello").getOrNull() shouldBe "hello"
        SingleResult.TooMany.getOrNull() shouldBe null
    }

    @Test
    fun testSingleResult() {
        sequenceOf<Int>().singleResult() shouldBe SingleResult.None
        sequenceOf(1).singleResult() shouldBe SingleResult.Success(1)
        sequenceOf(1,2).singleResult() shouldBe SingleResult.TooMany

        listOf<Int>().asIterable().singleResult() shouldBe SingleResult.None
        listOf(1).asIterable().singleResult() shouldBe SingleResult.Success(1)
        listOf(1,2).asIterable().singleResult() shouldBe SingleResult.TooMany

        listOf<Int>().singleResult() shouldBe SingleResult.None
        listOf(1).singleResult() shouldBe SingleResult.Success(1)
        listOf(1,2).singleResult() shouldBe SingleResult.TooMany
    }
}
import io.github.klahap.kotlin.util.containsAny
import io.github.klahap.kotlin.util.intersectAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CollectionTest {
    @Test
    fun `test containsAny`() {
        listOf(1, 2, 3, 4).containsAny(2, 7, 8) shouldBe true
        listOf(1, 2, 3, 4).containsAny(6, 7, 8) shouldBe false
        listOf(1, 2, 3, 4).containsAny(listOf(2, 7, 8)) shouldBe true
        listOf(1, 2, 3, 4).containsAny(listOf(6, 7, 8)) shouldBe false
    }

    @Test
    fun `test intersectAll`() {
        listOf(listOf(1,2,3), listOf(4,5,6), listOf(7,8,9)).intersectAll() shouldBe setOf()
        listOf(listOf(1,2,3), listOf(3,5,6), listOf(3,5,9)).intersectAll() shouldBe setOf(3)
        listOf(listOf(1,5,3), listOf(3,5,6), listOf(3,5,9)).intersectAll() shouldBe setOf(3, 5)
        intersectAll(listOf(1,2,3), listOf(4,5,6), listOf(7,8,9)) shouldBe setOf()
        intersectAll(listOf(1,2,3), listOf(3,5,6), listOf(3,5,9)) shouldBe setOf(3)
        intersectAll(listOf(1,5,3), listOf(3,5,6), listOf(3,5,9)) shouldBe setOf(3, 5)
    }
}

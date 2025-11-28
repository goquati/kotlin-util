import io.github.goquati.kotlin.util.asOrNull
import io.github.goquati.kotlin.util.containsAny
import io.github.goquati.kotlin.util.intersectAll
import io.github.goquati.kotlin.util.takeIfNotEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CollectionTest {
    @Test
    fun testTakeIfNotEmpty() {
        listOf(1,2,3).takeIfNotEmpty() shouldBe listOf(1,2,3)
        listOf<Int>().takeIfNotEmpty() shouldBe null
    }

    @Test
    fun testOrEmpty() {
        listOf(1,2,3).takeIf { false }.orEmpty() shouldBe listOf()
        listOf(1,2,3).takeIf { true }.orEmpty() shouldBe listOf(1,2,3)

        listOf(1,2,3).asOrNull<Collection<Int>>().takeIf { false }.orEmpty() shouldBe listOf()
        listOf(1,2,3).asOrNull<Collection<Int>>().takeIf { true }.orEmpty() shouldBe listOf(1,2,3)
    }

    @Test
    fun testContainsAny() {
        listOf(1, 2, 3, 4).containsAny(2, 7, 8) shouldBe true
        listOf(1, 2, 3, 4).containsAny(6, 7, 8) shouldBe false
        listOf(1, 2, 3, 4).containsAny(listOf(2, 7, 8)) shouldBe true
        listOf(1, 2, 3, 4).containsAny(listOf(6, 7, 8)) shouldBe false
    }

    @Test
    fun testIntersectAll() {
        listOf(listOf(1,2,3), listOf(4,5,6), listOf(7,8,9)).intersectAll() shouldBe setOf()
        listOf(listOf(1,2,3), listOf(3,5,6), listOf(3,5,9)).intersectAll() shouldBe setOf(3)
        listOf(listOf(1,5,3), listOf(3,5,6), listOf(3,5,9)).intersectAll() shouldBe setOf(3, 5)
        intersectAll(listOf(1,2,3), listOf(4,5,6), listOf(7,8,9)) shouldBe setOf()
        intersectAll(listOf(1,2,3), listOf(3,5,6), listOf(3,5,9)) shouldBe setOf(3)
        intersectAll(listOf(1,5,3), listOf(3,5,6), listOf(3,5,9)) shouldBe setOf(3, 5)
    }
}

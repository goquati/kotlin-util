import io.github.goquati.kotlin.util.eachMaxBy
import io.github.goquati.kotlin.util.eachMinBy
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class GroupingTest {
    @Test
    fun testEachMinBy() {
        listOf<String>()
            .groupingBy { it[0] }
            .eachMinBy { it.length } shouldBe
                emptyMap()

        listOf("apple", "banana", "cranberry", "avocado", "cherry")
            .groupingBy { it[0] }
            .eachMinBy { it.length } shouldBe
                mapOf(
                    'a' to "apple",
                    'b' to "banana",
                    'c' to "cherry",
                )
    }

    @Test
    fun testEachMaxBy() {
        listOf<String>()
            .groupingBy { it[0] }
            .eachMaxBy { it.length } shouldBe
                emptyMap()

        listOf("apple", "banana", "cranberry", "avocado", "cherry")
            .groupingBy { it[0] }
            .eachMaxBy { it.length } shouldBe
                mapOf(
                    'a' to "avocado",
                    'b' to "banana",
                    'c' to "cranberry",
                )
    }
}

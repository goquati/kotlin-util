import de.quati.kotlin.util.eachMaxBy
import de.quati.kotlin.util.eachMaxOf
import de.quati.kotlin.util.eachMinBy
import de.quati.kotlin.util.eachMinOf
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

    @Test
    fun testEachMinOf() {
        listOf<String>()
            .groupingBy { it[0] }
            .eachMinOf { it.length } shouldBe
                emptyMap()

        listOf("apple", "banana", "cranberry", "avocado", "cherry")
            .groupingBy { it[0] }
            .eachMinOf { it.length } shouldBe
                mapOf(
                    'a' to 5,
                    'b' to 6,
                    'c' to 6,
                )
    }

    @Test
    fun testEachMaxOf() {
        listOf<String>()
            .groupingBy { it[0] }
            .eachMaxOf { it.length } shouldBe
                emptyMap()

        listOf("apple", "banana", "cranberry", "avocado", "cherry")
            .groupingBy { it[0] }
            .eachMaxOf { it.length } shouldBe
                mapOf(
                    'a' to 7,
                    'b' to 6,
                    'c' to 9,
                )
    }
}

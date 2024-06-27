import io.github.klahap.kotlin.util.eachMaxBy
import io.github.klahap.kotlin.util.eachMinBy
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GroupingTest {
    @Test
    fun `test eachMinBy`() {
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
    fun `test eachMaxBy`() {
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

import io.github.klahap.kotlin.util.mapKeysNotNull
import io.github.klahap.kotlin.util.mapValuesNotNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class HashMapTest {
    @Test
    fun `test mapValuesNotNull`() {
        mapOf<String, Int>().mapValuesNotNull { it.value } shouldBe mapOf()
        mapOf("apple" to 5, "banana" to 6)
            .mapValuesNotNull { it.value.takeIf { v -> v < 5 } } shouldBe
                mapOf()
        mapOf("apple" to 5, "banana" to 6)
            .mapValuesNotNull { it.value.takeIf { v -> v < 6 } } shouldBe
                mapOf("apple" to 5)
    }
    @Test
    fun `test mapKeysNotNull`() {
        mapOf<String, Int>().mapKeysNotNull { it.value } shouldBe mapOf()
        mapOf(5 to "apple", 6 to "banana")
            .mapKeysNotNull { it.key.takeIf { v -> v < 5 } } shouldBe
                mapOf()
        mapOf(5 to "apple", 6 to "banana")
            .mapKeysNotNull { it.key.takeIf { v -> v < 6 } } shouldBe
                mapOf(5 to "apple")
    }
}
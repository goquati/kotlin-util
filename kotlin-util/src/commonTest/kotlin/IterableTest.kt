import de.quati.kotlin.util.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import kotlin.test.Test


class IterableTest {
    @Test
    fun testTake() {
        listOf<String>().take(0, default = "apple") shouldBe listOf()
        listOf<String>().take(3, default = "apple") shouldBe listOf("apple", "apple", "apple")
        listOf("banana").take(3, default = "apple") shouldBe listOf("banana", "apple", "apple")
        listOf("banana", "orange", "avocado").take(3, default = "apple") shouldBe listOf("banana", "orange", "avocado")
        listOf("banana", "orange", "avocado", "salad").take(3, default = "apple") shouldBe
                listOf("banana", "orange", "avocado")
    }

    @Test
    fun testIsDistinct() {
        listOf<String>().isDistinct() shouldBe true
        listOf("apple", "banana", "orange").isDistinct() shouldBe true
        listOf("apple", "banana", "avocado").isDistinct() shouldBe true
        listOf("apple", "banana", "apple").isDistinct() shouldBe false
    }

    @Test
    fun testIsDistinctBy() {
        listOf<String>().isDistinctBy { it[0] } shouldBe true
        listOf("apple", "banana", "orange").isDistinctBy { it[0] } shouldBe true
        listOf("apple", "banana", "avocado").isDistinctBy { it[0] } shouldBe false
        listOf("apple", "banana", "apple").isDistinctBy { it[0] } shouldBe false
    }

    @Test
    fun testGroupByNotNull() {
        listOf<String>().groupByNotNull { it.length } shouldBe mapOf()
        listOf<String?>(null).groupByNotNull { it?.length } shouldBe mapOf()
        listOf("apple", "banana", "orange").groupByNotNull { it.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
        listOf("apple", null, "banana", "orange").groupByNotNull { it?.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
    }

    @Test
    fun testGroupByNotNullWithValueTransform() {
        listOf<String?>(null).groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf()
        listOf("apple", "banana", "avocado").groupByNotNull({ it[0] }, { it.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        listOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        listOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) ?: 'c' }, { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        listOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) }, { it?.length ?: -1 }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
    }

    @Test
    fun testAssociateNotNull() {
        listOf<String>().associateNotNull { it.length to it } shouldBe mapOf()
        listOf<String?>(null).associateNotNull { it?.length to it } shouldBe mapOf()
        listOf<String?>(null).associateNotNull { it to it?.length } shouldBe mapOf()
        listOf("apple", "banana", "orange").associateNotNull { it to it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        listOf("apple", "banana", "avocado").associateNotNull { it.length to it } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        listOf("apple", null, "banana", "orange").associateNotNull { (it ?: "cherry") to it?.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        listOf("apple", null, "banana", "avocado").associateNotNull { it?.length to (it ?: "cherry") } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        listOf("apple", null, "banana", "orange")
            .associateNotNull { fruit -> fruit?.let { it to it.length } } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testAssociateByNotNull() {
        listOf<String>().associateByNotNull { it.length } shouldBe mapOf()
        listOf<String?>(null).associateByNotNull { it?.length } shouldBe mapOf()
        listOf("apple", "banana", "avocado").associateByNotNull { it.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        listOf("apple", null, "banana", "avocado").associateByNotNull { it?.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
    }

    @Test
    fun testAssociateWithNotNull() {
        listOf<String>().associateWithNotNull { it.length } shouldBe mapOf()
        listOf<String?>(null).associateWithNotNull { it?.length } shouldBe mapOf()
        listOf("apple", "banana", "orange").associateWithNotNull { it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        listOf("apple", null, "banana", "orange").associateWithNotNull { it?.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testMostFrequent() {
        listOf(1, 2, 3, 4, 3, 6).mostFrequent() shouldBe 3
        listOf(1, 2, 3, 4, 5, 6).mostFrequent() shouldBeIn listOf(1, 2, 3, 4, 5, 6)
        shouldThrowExactly<NoSuchElementException> {
            listOf<Int>().mostFrequent()
        }
    }

    @Test
    fun testMostFrequentOrNull() {
        listOf(1, 2, 3, 4, 3, 6).mostFrequentOrNull() shouldBe 3
        listOf(1, 2, 3, 4, 5, 6).mostFrequentOrNull() shouldBeIn listOf(1, 2, 3, 4, 5, 6)
        listOf<Int>().mostFrequentOrNull() shouldBe null
    }
}


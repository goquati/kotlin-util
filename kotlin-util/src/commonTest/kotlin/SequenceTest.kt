import io.github.goquati.kotlin.util.*
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SequenceTest {
    @Test
    fun testTake() {
        sequenceOf<String>().take(0, default = "apple").toList() shouldBe listOf()
        sequenceOf<String>().take(3, default = "apple").toList() shouldBe listOf("apple", "apple", "apple")
        sequenceOf("banana").take(3, default = "apple").toList() shouldBe listOf("banana", "apple", "apple")
        sequenceOf("banana", "orange", "avocado", "salad").take(3, default = "apple").toList() shouldBe
                listOf("banana", "orange", "avocado")
    }

    @Test
    fun testIsDistinct() {
        sequenceOf<String>().isDistinct() shouldBe true
        sequenceOf("apple", "banana", "orange").isDistinct() shouldBe true
        sequenceOf("apple", "banana", "avocado").isDistinct() shouldBe true
        sequenceOf("apple", "banana", "apple").isDistinct() shouldBe false
    }

    @Test
    fun testIsDistinctBy() {
        sequenceOf<String>().isDistinctBy { it[0] } shouldBe true
        sequenceOf("apple", "banana", "orange").isDistinctBy { it[0] } shouldBe true
        sequenceOf("apple", "banana", "avocado").isDistinctBy { it[0] } shouldBe false
        sequenceOf("apple", "banana", "apple").isDistinctBy { it[0] } shouldBe false
    }

    @Test
    fun testGroupByNotNull() {
        sequenceOf<String>().groupByNotNull { it.length } shouldBe mapOf()
        sequenceOf<String?>(null).groupByNotNull { it?.length } shouldBe mapOf()
        sequenceOf("apple", "banana", "orange").groupByNotNull { it.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
        sequenceOf("apple", null, "banana", "orange").groupByNotNull { it?.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
    }

    @Test
    fun testGroupByNotNullWithValueTransform() {
        sequenceOf<String?>(null).groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf()
        sequenceOf("apple", "banana", "avocado").groupByNotNull({ it[0] }, { it.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        sequenceOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        sequenceOf("apple", null, "banana", "avocado").groupByNotNull(
            { it?.get(0) ?: 'c' },
            { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        sequenceOf("apple", null, "banana", "avocado").groupByNotNull(
            { it?.get(0) },
            { it?.length ?: -1 }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
    }

    @Test
    fun testAssociateNotNull() {
        sequenceOf<String>().associateNotNull { it.length to it } shouldBe mapOf()
        sequenceOf<String?>(null).associateNotNull { it?.length to it } shouldBe mapOf()
        sequenceOf<String?>(null).associateNotNull { it to it?.length } shouldBe mapOf()
        sequenceOf("apple", "banana", "orange").associateNotNull { it to it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        sequenceOf("apple", "banana", "avocado").associateNotNull { it.length to it } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        sequenceOf("apple", null, "banana", "orange").associateNotNull {
            (it ?: "cherry") to it?.length
        } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        sequenceOf("apple", null, "banana", "avocado").associateNotNull {
            it?.length to (it ?: "cherry")
        } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        sequenceOf("apple", null, "banana", "orange")
            .associateNotNull { fruit -> fruit?.let { it to it.length } } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testAssociateByNotNull() {
        sequenceOf<String>().associateByNotNull { it.length } shouldBe mapOf()
        sequenceOf<String?>(null).associateByNotNull { it?.length } shouldBe mapOf()
        sequenceOf("apple", "banana", "avocado").associateByNotNull { it.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        sequenceOf("apple", null, "banana", "avocado").associateByNotNull { it?.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
    }

    @Test
    fun testAssociateWithNotNull() {
        sequenceOf<String>().associateWithNotNull { it.length } shouldBe mapOf()
        sequenceOf<String?>(null).associateWithNotNull { it?.length } shouldBe mapOf()
        sequenceOf("apple", "banana", "orange").associateWithNotNull { it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        sequenceOf("apple", null, "banana", "orange").associateWithNotNull { it?.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }
}
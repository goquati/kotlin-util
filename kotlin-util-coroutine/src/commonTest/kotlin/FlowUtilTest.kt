import io.github.goquati.kotlin.util.coroutine.*
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FlowUtilTest {
    @Test
    fun testEmitAll(): TestResult = runTest {
        flow { listOf(1, 2, 3).emitAll() }.toList() shouldBe listOf(1, 2, 3)
        flow { flowOf(1, 2, 3).emitAll() }.toList() shouldBe listOf(1, 2, 3)
    }

    @Test
    fun testIsEmpty(): TestResult = runTest {
        flowOf<Int>().isEmpty() shouldBe true
        flow {
            emit(1)
            throw Exception()
        }.isEmpty() shouldBe false
    }

    @Test
    fun testIsNotEmpty(): TestResult = runTest {
        flowOf<Int>().isNotEmpty() shouldBe false
        flow {
            emit(1)
            throw Exception()
        }.isNotEmpty() shouldBe true
    }

    @Test
    fun testFilterNotEmpty(): TestResult = runTest {
        flowOf(setOf(1), emptySet(), setOf(2, 3)).filterNotEmpty().toList() shouldBe listOf(setOf(1), setOf(2, 3))
    }

    @Test
    fun testGroup(): TestResult = runTest {
        flowOf<String>().groupByNotNull { it.length } shouldBe mapOf()
        flowOf("apple", "banana", "orange").groupBy { it.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
    }

    @Test
    fun testGroupByNotNull(): TestResult = runTest {
        flowOf<String>().groupByNotNull { it.length } shouldBe mapOf()
        flowOf<String?>(null).groupByNotNull { it?.length } shouldBe mapOf()
        flowOf("apple", "banana", "orange").groupByNotNull { it.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
        flowOf("apple", null, "banana", "orange").groupByNotNull { it?.length } shouldBe mapOf(
            5 to listOf("apple"),
            6 to listOf("banana", "orange")
        )
    }

    @Test
    fun testGroupByWithValueTransform(): TestResult = runTest {
        flowOf<String>().groupBy({ it[0] }, { it.length }) shouldBe mapOf()
        flowOf("apple", "banana", "avocado").groupBy({ it[0] }, { it.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
    }

    @Test
    fun testGroupByNotNullWithValueTransform(): TestResult = runTest {
        flowOf<String?>(null).groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf()
        flowOf("apple", "banana", "avocado").groupByNotNull({ it[0] }, { it.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        flowOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) }, { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        flowOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) ?: 'c' }, { it?.length }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
        flowOf("apple", null, "banana", "avocado").groupByNotNull({ it?.get(0) }, { it?.length ?: -1 }) shouldBe mapOf(
            'a' to listOf(5, 7),
            'b' to listOf(6)
        )
    }

    @Test
    fun testAssociate(): TestResult = runTest {
        flowOf<String>().associate { it.length to it } shouldBe mapOf()
        flowOf("apple", "banana", "orange").associate { it to it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        flowOf("apple", "banana", "avocado").associate { it.length to it } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
    }

    @Test
    fun testAssociateNotNull(): TestResult = runTest {
        flowOf<String>().associateNotNull { it.length to it } shouldBe mapOf()
        flowOf<String?>(null).associateNotNull { it?.length to it } shouldBe mapOf()
        flowOf<String?>(null).associateNotNull { it to it?.length } shouldBe mapOf()
        flowOf("apple", "banana", "orange").associateNotNull { it to it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        flowOf("apple", "banana", "avocado").associateNotNull { it.length to it } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        flowOf("apple", null, "banana", "orange").associateNotNull { (it ?: "cherry") to it?.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        flowOf("apple", null, "banana", "avocado").associateNotNull { it?.length to (it ?: "cherry") } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        flowOf("apple", null, "banana", "orange")
            .associateNotNull { fruit -> fruit?.let { it to it.length } } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testAssociateBy(): TestResult = runTest {
        flowOf<String>().associateBy { it.length } shouldBe mapOf()
        flowOf("apple", "banana", "avocado").associateBy { it.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
    }

    @Test
    fun testAssociateByNotNull(): TestResult = runTest {
        flowOf<String>().associateByNotNull { it.length } shouldBe mapOf()
        flowOf<String?>(null).associateByNotNull { it?.length } shouldBe mapOf()
        flowOf("apple", "banana", "avocado").associateByNotNull { it.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
        flowOf("apple", null, "banana", "avocado").associateByNotNull { it?.length } shouldBe mapOf(
            5 to "apple",
            6 to "banana",
            7 to "avocado",
        )
    }

    @Test
    fun testAssociateWithNotNull(): TestResult = runTest {
        flowOf<String>().associateWithNotNull { it.length } shouldBe mapOf()
        flowOf<String?>(null).associateWithNotNull { it?.length } shouldBe mapOf()
        flowOf("apple", "banana", "orange").associateWithNotNull { it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
        flowOf("apple", null, "banana", "orange").associateWithNotNull { it?.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testAssociateWith(): TestResult = runTest {
        flowOf<String>().associateWith { it.length } shouldBe mapOf()
        flowOf("apple", "banana", "orange").associateWith { it.length } shouldBe mapOf(
            "apple" to 5,
            "banana" to 6,
            "orange" to 6,
        )
    }

    @Test
    fun testWithIsLast(): TestResult = runTest {
        flowOf<String>().withIsLast().toList() shouldBe emptyList()
        flowOf("apple", "banana", "orange").withIsLast().toList() shouldBe listOf(
            WithIsLastValue(false, "apple"),
            WithIsLastValue(false, "banana"),
            WithIsLastValue(true, "orange"),
        )
    }

    @Test
    fun testWithIndexedAndIsLast(): TestResult = runTest {
        flowOf<String>().withIndexedAndIsLast().toList() shouldBe emptyList()
        flowOf("apple", "banana", "orange").withIndexedAndIsLast().toList() shouldBe listOf(
            IndexedWithIsLastValue(0, false, "apple"),
            IndexedWithIsLastValue(1, false, "banana"),
            IndexedWithIsLastValue(2, true, "orange"),
        )
    }
}
import io.github.goquati.kotlin.util.combine
import io.github.goquati.kotlin.util.flatten
import io.github.goquati.kotlin.util.mapKeysNotNull
import io.github.goquati.kotlin.util.mapValuesNotNull
import io.github.goquati.kotlin.util.takeIfNotEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HashMapTest {
    @Test
    fun testTakeIfNotEmpty() {
        mapOf(1 to "1", 2 to "2").takeIfNotEmpty() shouldBe mapOf(1 to "1", 2 to "2")
        mapOf<Int, String>().takeIfNotEmpty() shouldBe null
    }

    @Test
    fun testMapValuesNotNull() {
        mapOf<String, Int>().mapValuesNotNull { it.value } shouldBe mapOf()
        mapOf("apple" to 5, "banana" to 6)
            .mapValuesNotNull { it.value.takeIf { v -> v < 5 } } shouldBe
                mapOf()
        mapOf("apple" to 5, "banana" to 6)
            .mapValuesNotNull { it.value.takeIf { v -> v < 6 } } shouldBe
                mapOf("apple" to 5)
    }

    @Test
    fun testMapKeysNotNull() {
        mapOf<String, Int>().mapKeysNotNull { it.value } shouldBe mapOf()
        mapOf(5 to "apple", 6 to "banana")
            .mapKeysNotNull { it.key.takeIf { v -> v < 5 } } shouldBe
                mapOf()
        mapOf(5 to "apple", 6 to "banana")
            .mapKeysNotNull { it.key.takeIf { v -> v < 6 } } shouldBe
                mapOf(5 to "apple")
    }

    @Test
    fun testFlatten() {
        listOf(mapOf(1 to 10, 2 to 20), mapOf(3 to 30, 4 to 40)).flatten() shouldBe mapOf(
            1 to 10, 2 to 20, 3 to 30, 4 to 40
        )
        listOf(mapOf(1 to 10, 2 to 20), mapOf(2 to 20, 3 to 30, 4 to 40)).flatten() shouldBe mapOf(
            1 to 10, 2 to 20, 3 to 30, 4 to 40
        )
        listOf(mapOf(1 to 10)).flatten() shouldBe mapOf(1 to 10)
        listOf<Map<Int, Int>>().flatten() shouldBe mapOf()
    }

    @Test
    fun testCombine2Maps() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
        ) { key, t1, t2 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            "$key|$t1|$t2"
        } shouldBe mapOf(1 to "1|a|47", 2 to "2|b|12")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
        ) { key, t1, t2 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            "$key|$t1|$t2"
        } shouldBe mapOf(1 to "1|a|47")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47)
        ) { key, t1, t2 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            "$key|$t1|$t2"
        } shouldBe emptyMap()
    }

    @Test
    fun testCombine3Maps() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            "$key|$t1|$t2|$t3"
        } shouldBe mapOf(1 to "1|a|47|c", 2 to "2|b|12|d")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            "$key|$t1|$t2|$t3"
        } shouldBe mapOf(1 to "1|a|47|c")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            "$key|$t1|$t2|$t3"
        } shouldBe emptyMap()
    }

    @Test
    fun testCombine4Maps() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe mapOf(1 to "1|a|47|c|5", 2 to "2|b|12|d|6")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe mapOf(1 to "1|a|47|c|5")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe emptyMap()
    }

    @Test
    fun testCombine5Maps() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
            mapOf(1 to 7L, 2 to 8L),
        ) { key, t1, t2, t3, t4, t5 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            t5::class shouldBe Long::class
            "$key|$t1|$t2|$t3|$t4|$t5"
        } shouldBe mapOf(
            1 to "1|a|47|c|5|7",
            2 to "2|b|12|d|6|8"
        )
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
            mapOf(1 to 7L, 2 to 8L),
        ) { key, t1, t2, t3, t4, t5 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            t5::class shouldBe Long::class
            "$key|$t1|$t2|$t3|$t4|$t5"
        } shouldBe mapOf(1 to "1|a|47|c|5|7")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
            mapOf(1 to 7L, 2 to 8L),
        ) { key, t1, t2, t3, t4, t5 ->
            key::class shouldBe Int::class
            t1::class shouldBe String::class
            t2::class shouldBe Int::class
            t3::class shouldBe Char::class
            t4::class shouldBe UInt::class
            t5::class shouldBe Long::class
            "$key|$t1|$t2|$t3|$t4|$t5"
        } shouldBe emptyMap()
    }
}
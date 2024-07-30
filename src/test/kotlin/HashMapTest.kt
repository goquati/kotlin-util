import io.github.klahap.kotlin.util.combine
import io.github.klahap.kotlin.util.flatten
import io.github.klahap.kotlin.util.mapKeysNotNull
import io.github.klahap.kotlin.util.mapValuesNotNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

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

    @Test
    fun `test flatten`() {
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
    fun `test combine 2 maps`() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
        ) { key, t1, t2 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            "$key|$t1|$t2"
        } shouldBe mapOf(1 to "1|a|47", 2 to "2|b|12")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
        ) { key, t1, t2 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            "$key|$t1|$t2"
        } shouldBe mapOf(1 to "1|a|47")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47)
        ) { key, t1, t2 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            "$key|$t1|$t2"
        } shouldBe emptyMap()
    }

    @Test
    fun `test combine 3 maps`() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            "$key|$t1|$t2|$t3"
        } shouldBe mapOf(1 to "1|a|47|c", 2 to "2|b|12|d")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            "$key|$t1|$t2|$t3"
        } shouldBe mapOf(1 to "1|a|47|c")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
        ) { key, t1, t2, t3 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            "$key|$t1|$t2|$t3"
        } shouldBe emptyMap()
    }

    @Test
    fun `test combine 4 maps`() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe mapOf(1 to "1|a|47|c|5", 2 to "2|b|12|d|6")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe mapOf(1 to "1|a|47|c|5")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
        ) { key, t1, t2, t3, t4 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            "$key|$t1|$t2|$t3|$t4"
        } shouldBe emptyMap()
    }

    @Test
    fun `test combine 5 maps`() {
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(1 to 47, 2 to 12),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
            mapOf(1 to 7L, 2 to 8L),
        ) { key, t1, t2, t3, t4, t5 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            t5::class.java shouldBe Long::class.java
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
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            t5::class.java shouldBe Long::class.java
            "$key|$t1|$t2|$t3|$t4|$t5"
        } shouldBe mapOf(1 to "1|a|47|c|5|7")
        combine(
            mapOf(1 to "a", 2 to "b"),
            mapOf(5 to 47),
            mapOf(1 to 'c', 2 to 'd'),
            mapOf(1 to 5u, 2 to 6u),
            mapOf(1 to 7L, 2 to 8L),
        ) { key, t1, t2, t3, t4, t5 ->
            key::class.java shouldBe Int::class.java
            t1::class.java shouldBe String::class.java
            t2::class.java shouldBe Int::class.java
            t3::class.java shouldBe Char::class.java
            t4::class.java shouldBe UInt::class.java
            t5::class.java shouldBe Long::class.java
            "$key|$t1|$t2|$t3|$t4|$t5"
        } shouldBe emptyMap()
    }
}
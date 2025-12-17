import de.quati.kotlin.util.serialization.toKotlinValue
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class JsonElementUtilTest {
    @Test
    fun testToKotlinValue() {
        JsonObject(
            mapOf(
                "a" to JsonNull,
                "b" to JsonPrimitive(47),
                "c" to JsonPrimitive(48L),
                "c2" to JsonPrimitive(Int.MAX_VALUE.toLong() + 1),
                "d" to JsonPrimitive(true),
                "e" to JsonPrimitive(false),
                "f" to JsonPrimitive(3.14),
                "g" to JsonPrimitive(3.14f),
                "h" to JsonPrimitive("Hello World"),
                "i" to JsonArray(listOf(JsonPrimitive(47), JsonPrimitive("Foobar"))),
                "j" to JsonObject(mapOf("foo" to JsonPrimitive(47))),
            )
        ).toKotlinValue() shouldBe mapOf(
            "a" to null,
            "b" to 47,
            "c" to 48,
            "c2" to Int.MAX_VALUE.toLong() + 1,
            "d" to true,
            "e" to false,
            "f" to 3.14,
            "g" to 3.14,
            "h" to "Hello World",
            "i" to listOf(47, "Foobar"),
            "j" to mapOf("foo" to 47),
        )
    }
}
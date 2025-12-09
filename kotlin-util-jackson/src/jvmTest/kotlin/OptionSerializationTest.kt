import com.fasterxml.jackson.annotation.JsonUnwrapped
import de.quati.kotlin.util.Option
import de.quati.kotlin.util.jackson.QuatiOptionModule
import io.kotest.matchers.shouldBe
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import kotlin.test.Test

class OptionSerializationTest {
    private fun createMapper() = JsonMapper.builder()
        .addModule(QuatiOptionModule())
        .addModule(kotlinModule())
        .build()

    private inline fun <reified T : Any> JsonMapper.check(data: T, expected: String) {
        writeValueAsString(data) shouldBe expected
        readValue<T>(expected) shouldBe data
    }

    private inline fun <reified T : Any> JsonMapper.checkSerialize(data: T, expected: String) {
        writeValueAsString(data) shouldBe expected
    }

    @Test
    fun testBasic() {
        val mapper = createMapper()
        mapper.checkSerialize(Option.Some("hello"), "\"hello\"")
        mapper.checkSerialize(Option.Some(null), "null")
        mapper.checkSerialize(Option.Some<String?>("hello"), "\"hello\"")
        mapper.checkSerialize(Option.Some<String?>(null), "null")
        mapper.checkSerialize(Option.Undefined, "null")
    }

    @Test
    fun testObjectBasic() {
        val mapper = createMapper()
        mapper.check(ObjectString(Option.Some("hello")), "{\"value\":\"hello\"}")
        mapper.check(ObjectString(Option.Undefined), "{}")
        mapper.check(ObjectStringNullable(Option.Some("hello")), "{\"value\":\"hello\"}")
        mapper.check(ObjectStringNullable(Option.Some(null)), "{\"value\":null}")
        mapper.check(ObjectStringNullable(Option.Undefined), "{}")
    }

    @Test
    fun testObjectUnwrap() {
        val mapper = createMapper()
        val address = Address("street", "zip")
        val addressJson = "{\"street\":\"street\",\"zip\":\"zip\"}"
        mapper.check(ObjectUnwrap(Option.Some(address)), "{\"address\":$addressJson}")
        mapper.check(ObjectUnwrap(Option.Undefined), "{}")
        mapper.check(ObjectUnwrapNullable(Option.Some(address)), "{\"address\":$addressJson}")
        mapper.check(ObjectUnwrapNullable(Option.Some(null)), "{\"address\":null}")
        mapper.check(ObjectUnwrapNullable(Option.Undefined), "{}")
    }
}
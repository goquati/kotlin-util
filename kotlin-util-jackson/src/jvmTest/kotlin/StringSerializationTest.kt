import io.github.goquati.kotlin.util.jackson.QuatiJacksonStringSerializer
import io.github.goquati.kotlin.util.jackson.QuatiOptionModule
import io.github.goquati.kotlin.util.jackson.add
import io.github.goquati.kotlin.util.jackson.addSimpleModule
import io.kotest.matchers.shouldBe
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.Test


private object LocalDateSerializer : QuatiJacksonStringSerializer<LocalDate> {
    private val FORMATTER_DATE = DateTimeFormatter.ISO_LOCAL_DATE!!
    override val clazz = LocalDate::class
    override val serialize = FORMATTER_DATE::format
    override val deserialize: (String) -> LocalDate = { LocalDate.parse(it, FORMATTER_DATE)!! }
}

private object AddressSerializer : QuatiJacksonStringSerializer<Address> {
    override val clazz = Address::class
    override val serialize = Address::toString
    override val deserialize = Address::parse
}

class StringSerializationTest {
    private fun createMapper() = JsonMapper.builder()
        .addModule(QuatiOptionModule())
        .addModule(kotlinModule())
        .addSimpleModule {
            add(LocalDateSerializer)
            add(listOf(AddressSerializer))
        }
        .build()

    private inline fun <reified T : Any> JsonMapper.check(data: T, expected: String) {
        writeValueAsString(data) shouldBe expected
        readValue<T>(expected) shouldBe data
    }

    @Test
    fun testObjectBasic() {
        val mapper = createMapper()
        mapper.check(LocalDate.of(2000,10, 7), "\"2000-10-07\"")
        mapper.check(Address("street", "zip"), "\"street|zip\"")
    }
}
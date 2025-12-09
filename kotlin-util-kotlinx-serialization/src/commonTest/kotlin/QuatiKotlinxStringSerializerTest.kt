import io.github.goquati.kotlin.util.serialization.add
import io.github.goquati.kotlin.util.serialization.serializersModule
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
class QuatiKotlinxStringSerializerTest {
    fun createMapper() = Json {
        serializersModule {
            add(InstantSerializer)
            add(listOf(AddressSerializer))
        }
    }

    private inline fun <reified T : Any> Json.check(data: T, expected: String) {
        encodeToString(data) shouldBe expected
        decodeFromString<T>(expected) shouldBe data
    }

    @Test
    fun testSerializer() {
        AddressSerializer.descriptor.serialName shouldBe "AddressSerializer"
    }

    @Test
    fun testBasic() {
        val mapper = createMapper()
        mapper.check(Instant.fromEpochMilliseconds(100), "\"1970-01-01T00:00:00.100Z\"")
        mapper.check(Address("street", "zip"), "\"street|zip\"")
    }
}
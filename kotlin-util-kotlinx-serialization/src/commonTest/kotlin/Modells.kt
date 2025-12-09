import de.quati.kotlin.util.serialization.QuatiKotlinxStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Address(
    val street: String,
    val zip: String,
) {
    override fun toString(): String = "$street|$zip"

    companion object {
        fun parse(s: String): Address = s.split("|").let { Address(it[0], it[1]) }
    }
}


@OptIn(ExperimentalTime::class)
object InstantSerializer : QuatiKotlinxStringSerializer<Instant> {
    override val clazz = Instant::class
    override val serialize = Instant::toString
    override val deserialize = Instant::parse
}

object AddressSerializer : QuatiKotlinxStringSerializer<Address> {
    override val clazz = Address::class
    override val serialize = Address::toString
    override val deserialize = Address::parse
}


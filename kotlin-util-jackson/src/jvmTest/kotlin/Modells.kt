import com.fasterxml.jackson.annotation.JsonUnwrapped
import de.quati.kotlin.util.Option

data class ObjectString(
    val value: Option<String>
)

data class ObjectStringNullable(
    val value: Option<String?>
)

data class Address(
    val street: String,
    val zip: String,
) {
    override fun toString(): String = "$street|$zip"

    companion object {
        fun parse(s: String): Address = s.split("|").let { Address(it[0], it[1]) }
    }
}

data class ObjectUnwrap(
    @JsonUnwrapped
    val address: Option<Address>,
)

data class ObjectUnwrapNullable(
    @JsonUnwrapped
    val address: Option<Address?>,
)
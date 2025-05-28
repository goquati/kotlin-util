import io.github.goquati.kotlin.util.crypto.QuatiApiToken
import io.github.goquati.kotlin.util.crypto.QuatiApiTokenEncoder
import io.github.goquati.kotlin.util.crypto.QuatiApiTokenParsed
import io.github.goquati.kotlin.util.getOrThrow
import io.github.goquati.kotlin.util.isFailure
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TokenEncoderTest {

    @Test
    fun testBasic() {
        val encoder = QuatiApiTokenEncoder.createSimple()

        @OptIn(ExperimentalUuidApi::class)
        val tokenParsed = QuatiApiTokenParsed.Simple(
            product = "quati",
            version = "v3",
            env = "stg",
            id = Uuid.parse("6d797633-e609-4820-ac3f-e62ab1efb435"),
            secret = Uuid.parse("9bcdd265-c88d-432f-a721-bad829749538"),
            info = "hello world",
        )
        val token = tokenParsed.token
        val hash = encoder.encode(token)
        val tokenParsed2 = encoder.parse(token).getOrThrow()
        val match = encoder.matches(token, hash)

        hash.toString().startsWith("{pbkdf2}") shouldBe true
        token.toString() shouldBe "quati_v3_stg_6d797633e6094820ac3fe62ab1efb435_9bcdd265c88d432fa721bad829749538_aGVsbG8gd29ybGQ"
        tokenParsed shouldBe tokenParsed2
        tokenParsed.token shouldBe tokenParsed2.token
        match shouldBe true
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testParsing() {
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("invalid")).isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_invalidfb435_9bcdd265c88d432fa721bad829749538_aGVsbG8gd29ybGQ"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_6d797633e6094820ac3fe62ab1efb435_invalid749538_aGVsbG8gd29ybGQ"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_6d797633e6094820ac3fe62ab1efb435_9bcdd265c88d432fa721bad829749538_aGVsbG8=="))
            .isFailure() shouldBe true

        val token =
            QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_6d797633e6094820ac3fe62ab1efb435_9bcdd265c88d432fa721bad829749538_aGVsbG8gd29ybGQ"))
                .getOrThrow()
        token.product shouldBe "quati"
        token.version shouldBe "v3"
        token.env shouldBe "stg"
        token.id shouldBe Uuid.parse("6d797633-e609-4820-ac3f-e62ab1efb435")
        token.secret shouldBe Uuid.parse("9bcdd265-c88d-432f-a721-bad829749538")
        token.info shouldBe "hello world"
    }
}
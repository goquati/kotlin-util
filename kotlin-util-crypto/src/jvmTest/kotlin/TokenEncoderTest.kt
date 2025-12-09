import de.quati.kotlin.util.crypto.QuatiApiToken
import de.quati.kotlin.util.crypto.QuatiApiTokenEncoder
import de.quati.kotlin.util.crypto.QuatiApiTokenParsed
import de.quati.kotlin.util.crypto.QuatiApiTokenParsed.Companion.hint
import de.quati.kotlin.util.getOrThrow
import de.quati.kotlin.util.isFailure
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
        token.toString() shouldBe "quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZvN0mXIjUMvpyG62Cl0lThoZWxsbyB3b3JsZA"
        tokenParsed.hint shouldBe "quati_v3_stg_bXl...sZA"
        tokenParsed shouldBe tokenParsed2
        tokenParsed.token shouldBe tokenParsed2.token
        match shouldBe true
    }

    @Test
    fun testBasic2() {
        val encoder = QuatiApiTokenEncoder.createSimple()

        @OptIn(ExperimentalUuidApi::class)
        val tokenParsed = QuatiApiTokenParsed.Simple(
            product = "quati",
            version = "v3",
            env = "stg",
            id = Uuid.parse("6d797633-e609-4820-ac3f-e62ab1efb435"),
            secret = Uuid.parse("9bcdd265-c88d-432f-a721-bad829749538"),
            info = "abcÿxyzÿ123ÿ",
        )
        val token = tokenParsed.token
        val hash = encoder.encode(token)
        val tokenParsed2 = encoder.parse(token).getOrThrow()
        val match = encoder.matches(token, hash)

        hash.toString().startsWith("{pbkdf2}") shouldBe true
        token.toString() shouldBe "quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZvN0mXIjUMvpyG62Cl0lThhYmPDv3h5esOguMTIzw78"
        tokenParsed.hint shouldBe "quati_v3_stg_bXl...w78"
        tokenParsed shouldBe tokenParsed2
        tokenParsed.token shouldBe tokenParsed2.token
        match shouldBe true
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun testParsing() {
        val gg = QuatiApiTokenParsed.Simple(
            product = "quati",
            version = "v3",
            env = "stg",
            id = Uuid.parse("6d797633-e609-4820-ac3f-e62ab1efb435"),
            secret = Uuid.parse("9bcdd265-c88d-432f-a721-bad829749538"),
            info = "",
        ).token

        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("invalid")).isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_invalidfb435_9bcdd265"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZv"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_bXl2MgdYJSCCs"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZvN"))
            .isFailure() shouldBe true
        QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZvN0mXIjUMvpyG62Cl0lTga"))
            .getOrThrow()
        val token =
            QuatiApiTokenParsed.parseSimple(QuatiApiToken.Simple("quati_v3_stg_bXl2MgdYJSCCsPgdYqsegd0NZvN0mXIjUMvpyG62Cl0lThoZWxsbyB3b3JsZA"))
                .getOrThrow()
        token.product shouldBe "quati"
        token.version shouldBe "v3"
        token.env shouldBe "stg"
        token.id shouldBe Uuid.parse("6d797633-e609-4820-ac3f-e62ab1efb435")
        token.secret shouldBe Uuid.parse("9bcdd265-c88d-432f-a721-bad829749538")
        token.info shouldBe "hello world"
    }
}
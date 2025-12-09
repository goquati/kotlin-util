import de.quati.kotlin.util.fromBase64
import de.quati.kotlin.util.fromBase64UrlSafe
import de.quati.kotlin.util.toBase64
import de.quati.kotlin.util.toBase64UrlSafe
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EncodingTest {
    @Test
    fun testBase64() {
        "foobar".encodeToByteArray().toBase64() shouldBe "Zm9vYmFy"
        "foobar".encodeToByteArray().toBase64().fromBase64().decodeToString() shouldBe "foobar"

        "hello world".encodeToByteArray().toBase64() shouldBe "aGVsbG8gd29ybGQ="
        "hello world".encodeToByteArray().toBase64().fromBase64().decodeToString() shouldBe "hello world"

        val bytes = (200..255).map { it.toByte() }.toByteArray()
        bytes.toBase64() shouldBe "yMnKy8zNzs/Q0dLT1NXW19jZ2tvc3d7f4OHi4+Tl5ufo6err7O3u7/Dx8vP09fb3+Pn6+/z9/v8="
        bytes.toBase64().fromBase64() shouldBe bytes
    }

    @Test
    fun testBase64UrlSafe() {
        "foobar".encodeToByteArray().toBase64UrlSafe() shouldBe "Zm9vYmFy"
        "foobar".encodeToByteArray().toBase64UrlSafe().fromBase64UrlSafe().decodeToString() shouldBe "foobar"

        "hello world".encodeToByteArray().toBase64UrlSafe() shouldBe "aGVsbG8gd29ybGQ"
        "hello world".encodeToByteArray().toBase64UrlSafe().fromBase64UrlSafe().decodeToString() shouldBe "hello world"

        val bytes = (200..255).map { it.toByte() }.toByteArray()
        bytes.toBase64UrlSafe() shouldBe "yMnKy8zNzs_Q0dLT1NXW19jZ2tvc3d7f4OHi4-Tl5ufo6err7O3u7_Dx8vP09fb3-Pn6-_z9_v8"
        bytes.toBase64UrlSafe().fromBase64UrlSafe() shouldBe bytes
    }
}
import kotlin.test.Test
import io.github.goquati.kotlin.util.AesEncryption
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe


class EncryptionTest {
    companion object {
        private val charset = Charsets.UTF_8
        private val key = AesEncryption.Key(
            value = "ik5INDnU0rAVGPuBqskiLupBAqGHH_3VgAD_G8qy--0",
            iv = "-Em_EhBcTUC7sGPWNf_fyw"
        )

        private fun test(clearText: String, cipherText: String) {
            val c = AesEncryption(key)
            c.encrypt(clearText.toByteArray(charset)) shouldBe cipherText
            c.encryptString(clearText) shouldBe cipherText
            c.encryptString(clearText).let { c.decryptString(it) } shouldBe clearText
            c.encryptString(clearText).let { c.decrypt(it).toString(charset) } shouldBe clearText
        }
    }

    @Test
    fun testAesEncryptionKeyGen() {
        val key = AesEncryption.Key.random()
        key shouldNotBe EncryptionTest.key

        val c = AesEncryption(key)
        c.encryptString("foobar").let { c.decryptString(it) } shouldBe "foobar"
    }

    @Test
    fun testAesEncryption() {
        test(clearText = "foobar", cipherText = "Wpf89FHaSxcvKwiggSlcgw")
        test(clearText = "hello world", cipherText = "WKaIegJ4f4_8ml7aiw-EDg")
    }
}
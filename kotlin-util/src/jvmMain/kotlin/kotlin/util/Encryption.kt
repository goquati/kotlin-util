package io.github.goquati.kotlin.util

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


public class AesEncryption(key: Key) {
    private val key: SecretKey = SecretKeySpec(key.value.fromBase64UrlSafe(), ALGORITHM)
    private val iv: IvParameterSpec = IvParameterSpec(key.iv.fromBase64UrlSafe())

    public fun encrypt(data: ByteArray): String = processAES(data, Cipher.ENCRYPT_MODE).toBase64UrlSafe()
    public fun encryptString(data: String): String = encrypt(data.toByteArray(charset))

    public fun decrypt(data: String): ByteArray = processAES(data.fromBase64UrlSafe(), Cipher.DECRYPT_MODE)
    public fun decryptString(data: String): String = decrypt(data).toString(charset)

    private fun processAES(data: ByteArray, mode: Int): ByteArray {
        val cipher = Cipher.getInstance("$ALGORITHM/CBC/PKCS5Padding")
        cipher.init(mode, key, iv)
        return cipher.doFinal(data)
    }

    public data class Key(
        val value: String,
        val iv: String
    ) {
        public companion object {
            public fun random(): Key = Key(
                value = KeyGenerator.getInstance(ALGORITHM).apply { init(256) }.generateKey().encoded.toBase64UrlSafe(),
                iv = IvParameterSpec(ByteArray(16).also { SecureRandom().nextBytes(it) }).iv.toBase64UrlSafe(),
            )
        }
    }

    private companion object {
        private const val ALGORITHM = "AES"
        private val charset = Charsets.UTF_8
    }
}
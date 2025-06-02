package io.github.goquati.kotlin.util.crypto

import kotlin.io.encoding.Base64

internal object Base64Escaped {
    private val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
    fun encode(data: ByteArray): String {
        val dataBase64 = encoder.encode(data)
        val dataStr = dataBase64
            .replace("g", "ga")
            .replace("-", "gd")
            .replace("_", "gu")
        return dataStr
    }

    fun decode(data: String): ByteArray {
        val dataBase64 = data
            .replace("gd", "-")
            .replace("gu", "_")
            .replace("ga", "g")
        val dataBytes = encoder.decode(dataBase64)
        return dataBytes
    }
}
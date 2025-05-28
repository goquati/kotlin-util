package io.github.goquati.kotlin.util.crypto

import kotlin.io.encoding.Base64
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.util.crypto.QuatiApiTokenParsed.Companion.infoEncoder

public interface QuatiApiTokenHash {
    public val value: String

    @JvmInline
    public value class Simple(override val value: String) : QuatiApiTokenHash {
        override fun toString(): String = value
    }
}

public interface QuatiApiToken {
    public val value: String

    @JvmInline
    public value class Simple(override val value: String) : QuatiApiToken {
        override fun toString(): String = value
    }
}

@OptIn(ExperimentalUuidApi::class)
public val QuatiApiTokenParsed.token: QuatiApiToken
    get() {
        val infoEncoded = infoEncoder.encode(info.toByteArray())
        val token = "${product}_${version}_${env}_${id.toHexString()}_${secret.toHexString()}_$infoEncoded"
        return QuatiApiToken.Simple(token)
    }

@OptIn(ExperimentalUuidApi::class)
public interface QuatiApiTokenParsed {
    public val product: String
    public val version: String
    public val env: String
    public val id: Uuid
    public val secret: Uuid
    public val info: String

    @OptIn(ExperimentalUuidApi::class)
    public data class Simple(
        override val product: String,
        override val version: String,
        override val env: String,
        override val id: Uuid = Uuid.random(),
        override val secret: Uuid = Uuid.random(),
        override val info: String,
    ) : QuatiApiTokenParsed

    public companion object {
        internal val infoEncoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

        @OptIn(ExperimentalUuidApi::class)
        public fun parseSimple(token: QuatiApiToken): Result<Simple, InvalidApiTokenException> {
            val parts = token.value.split("_").toTypedArray().takeIf { it.size == 6 }
                ?: return Failure(InvalidApiTokenException("invalid number of parts"))
            val id = runCatching { Uuid.parseHex(parts[3]) }
                .getOrElse { return Failure(InvalidApiTokenException("invalid id")) }
            val secret = runCatching { Uuid.parseHex(parts[4]) }
                .getOrElse { return Failure(InvalidApiTokenException("invalid secret")) }
            val info = runCatching { infoEncoder.decode(parts[5]).decodeToString() }
                .getOrElse { return Failure(InvalidApiTokenException("invalid info")) }
            val token = Simple(
                product = parts[0],
                version = parts[1],
                env = parts[2],
                id = id,
                secret = secret,
                info = info,
            )
            return Success(token)
        }
    }
}

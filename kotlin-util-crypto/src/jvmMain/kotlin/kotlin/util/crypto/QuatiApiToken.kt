package io.github.goquati.kotlin.util.crypto

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import io.github.goquati.kotlin.util.Result
import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Success

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
public interface QuatiApiTokenParsed {
    public val product: String
    public val version: String
    public val env: String
    public val id: Uuid
    public val secret: Uuid
    public val info: String
    public val token: QuatiApiToken

    @OptIn(ExperimentalUuidApi::class)
    public data class Simple(
        override val product: String,
        override val version: String,
        override val env: String,
        override val id: Uuid = Uuid.random(),
        override val secret: Uuid = Uuid.random(),
        override val info: String,
    ) : QuatiApiTokenParsed {
        override val token: QuatiApiToken.Simple get() = simpleToken
    }

    public companion object {
        private val QuatiApiTokenParsed.header: String get() = "${product}_${version}_$env"
        private val QuatiApiTokenParsed.body: String
            get() {
                val bodyStr = Base64Escaped.encode(id.toByteArray() + secret.toByteArray() + info.toByteArray())
                return bodyStr
            }

        public val QuatiApiTokenParsed.simpleToken: QuatiApiToken.Simple
            get() {
                val token = "${header}_$body"
                return QuatiApiToken.Simple(token)
            }

        public val QuatiApiTokenParsed.hint: String
            get() {
                val bodyHint = body.let { "${it.take(3)}...${it.takeLast(3)}" }
                val hint = "${header}_$bodyHint"
                return hint
            }

        @OptIn(ExperimentalUuidApi::class)
        public fun parseSimple(token: QuatiApiToken): Result<Simple, InvalidApiTokenException> {
            val (product, version, env, bodyStr) = token.value.split("_").toTypedArray().takeIf { it.size == 4 }
                ?: return Failure(InvalidApiTokenException("invalid number of parts"))
            val bodyData = runCatching { Base64Escaped.decode(bodyStr) }
                .getOrElse { return Failure(InvalidApiTokenException("invalid body")) }
            val id = runCatching { Uuid.fromByteArray(bodyData.copyOfRange(0, 16)) }
                .getOrElse { return Failure(InvalidApiTokenException("invalid id")) }
            val secret = runCatching { Uuid.fromByteArray(bodyData.copyOfRange(16, 32)) }
                .getOrElse { return Failure(InvalidApiTokenException("invalid secret")) }
            val info = bodyData.copyOfRange(32, bodyData.size).decodeToString()
            val token = Simple(
                product = product,
                version = version,
                env = env,
                id = id,
                secret = secret,
                info = info,
            )
            return Success(token)
        }
    }
}

package de.quati.kotlin.util.crypto

import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import de.quati.kotlin.util.Result
import de.quati.kotlin.util.Success
import de.quati.kotlin.util.Failure
import de.quati.kotlin.util.getOr

public class QuatiApiTokenEncoder<Token : QuatiApiToken, TokenHash : QuatiApiTokenHash, TokenParsed : QuatiApiTokenParsed>(
    private val hasher: PasswordEncoder = defaultEncoder,
    private val parser: (token: QuatiApiTokenParsed.Simple) -> Result<TokenParsed, InvalidApiTokenException>,
    private val hashMapper: (token: QuatiApiTokenHash.Simple) -> TokenHash,
) {
    public fun parse(token: Token): Result<TokenParsed, InvalidApiTokenException> {
        val simpleParsedToken = QuatiApiTokenParsed.parseSimple(token).getOr { return Failure(it) }
        val parsedToken = parser(simpleParsedToken).getOr { return Failure(it) }
        return Success(parsedToken)
    }

    public fun encode(token: Token): TokenHash {
        val hashString = hasher.encode(token.value)!!
        val hashSimple = QuatiApiTokenHash.Simple(hashString)
        val hash = hashMapper(hashSimple)
        return hash
    }

    public fun matches(token: Token, hash: TokenHash): Boolean =
        hasher.matches(token.value, hash.value)

    public companion object {
        private val defaultEncoder: DelegatingPasswordEncoder
            get() {
                val encoder = DelegatingPasswordEncoder(
                    "pbkdf2", mapOf(
                        "pbkdf2" to Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
                    )
                )
                return encoder
            }

        public fun createSimple(): QuatiApiTokenEncoder<QuatiApiToken, QuatiApiTokenHash, QuatiApiTokenParsed> =
            QuatiApiTokenEncoder(
                parser = { Success(it) },
                hashMapper = { it }
            )
    }
}

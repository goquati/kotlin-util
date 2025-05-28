package io.github.goquati.kotlin.util.crypto

public open class InvalidApiTokenException(message: String) : RuntimeException("Invalid API token: $message")

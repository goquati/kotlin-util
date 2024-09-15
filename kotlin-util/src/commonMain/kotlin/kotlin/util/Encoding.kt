package io.github.goquati.kotlin.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
private val urlSafeBase64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

@OptIn(ExperimentalEncodingApi::class)
public fun ByteArray.toBase64(): String = Base64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
public fun ByteArray.toBase64UrlSafe(): String = urlSafeBase64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
public fun String.fromBase64(): ByteArray = Base64.decode(this)

@OptIn(ExperimentalEncodingApi::class)
public fun String.fromBase64UrlSafe(): ByteArray = urlSafeBase64.decode(this)

package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel


public suspend fun <T> Iterable<T>.toReceiveChannel(capacity: Int = Channel.UNLIMITED): ReceiveChannel<T> =
    Channel<T>(capacity = capacity).apply {
        this@toReceiveChannel.forEach { send(it) }
        close()
    }

public suspend fun <T> Collection<T>.toReceiveChannel(): ReceiveChannel<T> = toReceiveChannel(capacity = size)

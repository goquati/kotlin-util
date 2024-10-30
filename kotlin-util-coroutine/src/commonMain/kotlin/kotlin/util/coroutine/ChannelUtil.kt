package io.github.goquati.kotlin.util.coroutine

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel


public suspend fun <T> Iterable<T>.toClosedChannel(capacity: Int = Channel.UNLIMITED): ReceiveChannel<T> =
    Channel<T>(capacity = capacity).apply {
        this@toClosedChannel.forEach { send(it) }
        close()
    }

public suspend fun <T> Collection<T>.toClosedChannel(): ReceiveChannel<T> = toClosedChannel(capacity = size)

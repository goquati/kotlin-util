package de.quati.kotlin.util.coroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

context(scope: ProducerScope<T>)
public suspend fun <T> Iterable<T>.sendAll() {
    forEach { scope.send(it) }
}

context(scope: ProducerScope<T>)
public suspend fun <T> Flow<T>.sendAll() {
    collect { scope.send(it) }
}

public suspend fun <T> Iterable<T>.toReceiveChannel(capacity: Int = Channel.UNLIMITED): ReceiveChannel<T> =
    Channel<T>(capacity = capacity).apply {
        this@toReceiveChannel.forEach { send(it) }
        close()
    }

public suspend fun <T> Collection<T>.toReceiveChannel(): ReceiveChannel<T> = toReceiveChannel(capacity = size)

public fun <T> ReceiveChannel<T>.toBatchedFlow(maxBatchSize: Int? = null): Flow<List<T>> = flow {
    while (true) {
        val batch = mutableListOf<T>()
        try {
            batch.add(this@toBatchedFlow.receive())
        } catch (e: ClosedReceiveChannelException) {
            return@flow
        } catch (e: CancellationException) {
            return@flow
        }
        for (i in 1..<(maxBatchSize ?: Int.MAX_VALUE)) {
            var isChannelCurrentlyEmpty = false
            this@toBatchedFlow.tryReceive()
                .onSuccess { batch.add(it) }
                .onFailure { isChannelCurrentlyEmpty = true }
                .onClosed {
                    this@flow.emit(batch)
                    return@flow
                }
            if (isChannelCurrentlyEmpty) break
        }
        this@flow.emit(batch)
    }
}

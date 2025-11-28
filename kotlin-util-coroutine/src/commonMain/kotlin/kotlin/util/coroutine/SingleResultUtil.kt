package io.github.goquati.kotlin.util.coroutine

import io.github.goquati.kotlin.util.SingleResult
import io.github.goquati.kotlin.util.singleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

public suspend fun <T> Flow<T>.singleResult(): SingleResult<T> = take(2).toList().singleResult()

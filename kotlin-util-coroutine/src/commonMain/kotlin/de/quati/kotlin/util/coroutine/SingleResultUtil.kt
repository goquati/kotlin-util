package de.quati.kotlin.util.coroutine

import de.quati.kotlin.util.SingleResult
import de.quati.kotlin.util.singleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

public suspend fun <T> Flow<T>.singleResult(): SingleResult<T> = take(2).toList().singleResult()

package io.github.goquati.kotlin.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
public class WithTimestamp<T : Any>(
    public val value: T,
    public val timestamp: Instant,
) {
    public constructor(value: T) : this(value, Clock.System.now())
}

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TestClock : Clock {
    var current = Instant.fromEpochMilliseconds(0)
    fun plus(d: Duration) {
        current = current.plus(d)
    }

    override fun now() = current
}
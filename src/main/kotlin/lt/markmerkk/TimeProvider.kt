package lt.markmerkk

import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

interface TimeProvider {
    fun now(): OffsetDateTime
    fun fromInstant(instant: Instant): OffsetDateTime
}

class TimeProviderImpl(
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val clock: Clock = Clock.system(zoneId),
): TimeProvider {
    override fun now(): OffsetDateTime = OffsetDateTime.now(clock)

    override fun fromInstant(instant: Instant): OffsetDateTime {
        return instant.atZone(zoneId).toOffsetDateTime()
    }
}
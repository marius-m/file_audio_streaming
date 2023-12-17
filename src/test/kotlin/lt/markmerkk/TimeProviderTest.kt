package lt.markmerkk

import java.time.*
import java.time.Instant.ofEpochMilli

object TimeProviderTest : TimeProvider {
    val clock: Clock = Clock.fixed(ofEpochMilli(0), ZoneOffset.UTC)

    override fun now(): OffsetDateTime = OffsetDateTime.now(clock)

    override fun fromInstant(instant: Instant): OffsetDateTime {
        return instant.atZone(ZoneOffset.UTC).toOffsetDateTime()
    }
}
package lt.markmerkk.file_audio_streamer

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    val dtFormatterAsDateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dtFormatterBasic = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dtFormatterFull = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun legacyDateToOdt(date: java.util.Date): OffsetDateTime {
        return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC)
    }

    fun parseFromString(dateAsString: String): OffsetDateTime {
        return OffsetDateTime.from(dtFormatterFull.parse(dateAsString))
    }

    fun formatToString(odt: OffsetDateTime): String {
        return dtFormatterFull.format(odt)
    }

    fun formatToStringAsBasic(odt: OffsetDateTime): String {
        return dtFormatterBasic.format(odt)
    }
}

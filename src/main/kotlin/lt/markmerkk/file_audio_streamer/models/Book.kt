package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.UtilsLetters
import java.time.OffsetDateTime

data class Book(
    val categoryId: String,
    val id: String,
    val title: String,
    val path: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    val titleEng: String = UtilsLetters.transliterateLowercase(title)

    val createdAtBasicAsString = DateTimeUtils.formatToStringAsBasic(createdAt)
    val updatedAtBasicAsString = DateTimeUtils.formatToStringAsBasic(updatedAt)
}
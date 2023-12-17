package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.UtilsLetters
import java.time.OffsetDateTime

sealed class Category(
    val id: String,
    val title: String,
) {
    val titleEng: String = UtilsLetters.transliterateLowercase(title)
}

/**
 * Category that binds books with custom functionality
 * ex.: Custom queries - books from last year
 */
data class CategoryCustom(
    private val _id: String,
    private val _title: String,
    val type: CategoryCustomType,
) : Category(
    id = _id,
    title = _title,
)

/**
 * Category that binds with a file system
 */
data class CategoryFile(
    val rootEntryId: String,
    private val _id: String,
    private val _title: String,
    val path: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) : Category(
    id = _id,
    title = _title,
) {
    val createdAtBasicAsString = DateTimeUtils.formatToStringAsBasic(createdAt)
    val updatedAtBasicAsString = DateTimeUtils.formatToStringAsBasic(updatedAt)
}

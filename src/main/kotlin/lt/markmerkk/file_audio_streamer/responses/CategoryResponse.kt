package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.models.Category

data class CategoryResponse(
    val id: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(category: Category): CategoryResponse = CategoryResponse(
            id = category.id,
            title = category.title,
            createdAt = DateTimeUtils.formatToString(category.createdAt),
            updatedAt = DateTimeUtils.formatToString(category.updatedAt),
        )
    }
}
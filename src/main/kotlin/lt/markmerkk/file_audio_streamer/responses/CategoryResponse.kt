package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Category

data class CategoryResponse(
        val id: Int,
        val title: String
) {
    companion object {
        fun from(category: Category): CategoryResponse = CategoryResponse(
                id = category.index,
                title = category.title
        )
    }
}
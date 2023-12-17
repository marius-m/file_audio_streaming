package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.models.Book

data class BookResponse(
    val id: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {
        fun from(book: Book): BookResponse = BookResponse(
            id = book.id,
            title = book.title,
            createdAt = DateTimeUtils.formatToString(book.createdAt),
            updatedAt = DateTimeUtils.formatToString(book.updatedAt),
        )
    }
}
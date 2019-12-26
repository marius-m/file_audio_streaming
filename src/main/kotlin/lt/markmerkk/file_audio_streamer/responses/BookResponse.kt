package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Book2

data class BookResponse(
        val id: String,
        val title: String
) {
    companion object {
        fun from(book: Book2): BookResponse = BookResponse(
                id = book.id,
                title = book.title
        )
    }
}
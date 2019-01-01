package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Book

data class BookResponse(
        val id: Int,
        val title: String
) {
    companion object {
        fun from(book: Book): BookResponse = BookResponse(
                id = book.index,
                title = book.title
        )
    }
}
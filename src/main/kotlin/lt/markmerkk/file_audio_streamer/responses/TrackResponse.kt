package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Track

data class TrackResponse(
        val bookId: Int,
        val id: Int,
        val title: String
) {
    companion object {
        fun from(book: Book, track: Track): TrackResponse = TrackResponse(
                bookId = book.index,
                id = track.index,
                title = track.title
        )
    }
}
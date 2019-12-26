package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Track2

data class TrackResponse(
        val bookId: String,
        val id: String,
        val title: String
) {
    companion object {
        fun from(track: Track2): TrackResponse = TrackResponse(
                bookId = track.bookId,
                id = track.id,
                title = track.title
        )
    }
}
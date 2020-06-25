package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Track

data class TrackResponse(
        val bookId: String,
        val id: String,
        val title: String
) {
    companion object {
        fun from(track: Track): TrackResponse = TrackResponse(
                bookId = track.bookId,
                id = track.id,
                title = track.title
        )
    }
}
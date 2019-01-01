package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.models.Track

data class TrackResponse(
        val id: Int,
        val title: String
) {
    companion object {
        fun from(track: Track): TrackResponse = TrackResponse(
                id = track.index,
                title = track.title
        )
    }
}
package lt.markmerkk.file_audio_streamer.models

import java.io.File

data class Track(
        val rawTitle: String,
        val index: Int,
        val path: String
) {
    val title = rawTitle
            .replace(".mp3", "")
            .replace(".wav", "")

    companion object {
        fun from(fileIndex: Int, file: File): Track {
            return Track(
                    rawTitle = file.name,
                    index = fileIndex,
                    path = file.absolutePath
            )
        }
    }

}
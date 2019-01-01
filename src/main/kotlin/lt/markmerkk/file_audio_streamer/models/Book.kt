package lt.markmerkk.file_audio_streamer.models

import java.io.File

data class Book(
        val index: Int,
        val title: String
) {
    companion object {
        fun from(fileIndex: Int, file: File): Book {
            return Book(
                    index = fileIndex,
                    title = file.name
            )
        }
    }
}
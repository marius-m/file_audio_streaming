package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.Consts
import java.io.File

data class Track(
        val bookIndex: Int,
        val rawFileName: String,
        val index: Int,
        val path: String
) {
    val title = extractTitle()

    companion object {
        fun from(bookFileIndex: Int, fileIndex: Int, file: File): Track {
            return Track(
                    bookIndex = bookFileIndex,
                    rawFileName = file.name,
                    index = fileIndex,
                    path = file.absolutePath
            )
        }
    }

    /**
     * Extracts a title
     */
    internal fun extractTitle(): String {
        return Consts.supportedExtensions
                .fold(initial = rawFileName, operation = { title, extension ->
                    title.replace(
                            oldValue = ".$extension",
                            newValue = "",
                            ignoreCase = true
                    )
                })
    }

    /**
     * Check if track is of supported type
     */
    internal fun isSupported(): Boolean {
        return Consts.supportedExtensions
                .firstOrNull { supportedExtension ->
                    rawFileName.contains(".$supportedExtension", ignoreCase = true)
                } != null
    }

}


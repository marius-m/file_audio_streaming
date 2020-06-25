package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.Consts
import lt.markmerkk.file_audio_streamer.UtilsLetters

data class Track2(
        val bookId: String,
        val id: String,
        val rawFileName: String,
        val path: String
) {

    val title = extractTitle()

    val titleEng: String = UtilsLetters.transliterateLowercase(title)

    /**
     * Check if track is of supported type
     */
    internal fun isSupported(): Boolean {
        val hasExtension = Consts.supportedExtensions
                .firstOrNull { supportedExtension ->
                    rawFileName.contains(".$supportedExtension", ignoreCase = true)
                } != null
        val hasMetadataPrefix = rawFileName.startsWith("._")
        return hasExtension && !hasMetadataPrefix
    }

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
}
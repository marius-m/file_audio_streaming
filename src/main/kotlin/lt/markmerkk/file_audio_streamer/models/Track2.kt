package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.Consts

data class Track2(
        val bookId: String,
        val id: String,
        val rawFileName: String,
        val path: String
) {

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
}
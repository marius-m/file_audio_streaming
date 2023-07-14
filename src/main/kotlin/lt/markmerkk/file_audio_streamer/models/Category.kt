package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.UtilsLetters

data class Category(
    val rootEntryId: String,
    val id: String,
    val title: String,
    val path: String
) {
    val titleEng: String = UtilsLetters.transliterateLowercase(title)
}

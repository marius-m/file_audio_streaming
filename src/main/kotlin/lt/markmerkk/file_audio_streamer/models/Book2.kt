package lt.markmerkk.file_audio_streamer.models

data class Book2(
        val categoryId: String,
        val id: String,
        val title: String,
        val path: String
) {
    val titleEng: String = lt.markmerkk.file_audio_streamer.UtilsLetters.transliterateLowercase(title)
}
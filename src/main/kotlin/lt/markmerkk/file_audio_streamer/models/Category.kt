package lt.markmerkk.file_audio_streamer.models

data class Category(
        val id: String,
        val title: String,
        val path: String
) {
    companion object {

//        fun from(fileIndex: Int, file: File): Category {
//            return Category(
//                    index = fileIndex,
//                    title = file.name
//            )
//        }
    }
}
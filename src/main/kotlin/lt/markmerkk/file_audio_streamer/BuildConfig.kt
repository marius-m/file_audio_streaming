package lt.markmerkk.file_audio_streamer

data class BuildConfig private constructor(
    val contextPath: String
) {
    companion object {
        fun build(contextPath: String): BuildConfig {
            return BuildConfig(
                contextPath = contextPath
            )
        }
    }
}
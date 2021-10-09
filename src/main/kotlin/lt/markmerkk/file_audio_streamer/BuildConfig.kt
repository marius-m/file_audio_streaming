package lt.markmerkk.file_audio_streamer

data class BuildConfig private constructor(
    val contextPath: String
) {
    companion object {
        fun build(contextPath: String): BuildConfig {
            val ctxPath = if (contextPath.isEmpty()) {
                "/"
            } else {
                contextPath
            }
            return BuildConfig(
                contextPath = ctxPath
            )
        }
    }
}
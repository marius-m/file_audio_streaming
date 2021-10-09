package lt.markmerkk.file_audio_streamer.fs.entities

import org.apache.commons.lang3.time.StopWatch

data class IndexStatus(
    val isRunning: Boolean,
    val indexStats: IndexStats,
    val statusMessage: String,
) {
    companion object {
        fun build(
            indexStats: IndexStats,
            isRunning: Boolean,
            sw: StopWatch,
        ): IndexStatus {
            val statusMessage = if (isRunning) {
                "Indexing audio file system (${sw.time}ms). ${indexStats.reportInline()}"
            } else {
                ""
            }
            return IndexStatus(
                indexStats = indexStats,
                isRunning = isRunning,
                statusMessage = statusMessage,
            )
        }
    }
}
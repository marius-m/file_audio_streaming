package lt.markmerkk.file_audio_streamer.responses

import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.models.RootEntry

data class HealthResponse(
    val status: Status,
    val rootEntriesHealth: List<RootEntryHealth>
) {
    enum class Status {
        OK,
        UNHEALTHY_ROOT_ENTRY,
        ;
    }

    data class RootEntryHealth(
        val rootEntry: RootEntry,
        val health: FileHealth,
    ) {
        fun isHealthy(): Boolean = health.exists

        companion object {
            fun from(
                fsInteractor: FSInteractor,
                rootEntry: RootEntry,
            ): RootEntryHealth {
                return RootEntryHealth(
                    rootEntry = rootEntry,
                    health = FileHealth.from(fsInteractor, rootEntry.path),
                )
            }
        }
    }

    data class FileHealth(val exists: Boolean) {
        companion object {
            fun from(
                fsInteractor: FSInteractor,
                filePath: String,
            ): FileHealth {
                val dir = fsInteractor.resolvePathAsHealthyDirectoryOrNull(filePath)
                return FileHealth(exists = dir != null)
            }
        }
    }

    companion object {
        fun statusFromRootEntries(rootEntriesHealth: List<RootEntryHealth>): Status {
            val hasUnhealthyPaths = rootEntriesHealth
                .filter { !it.isHealthy() }
                .isNotEmpty()
            return when {
                hasUnhealthyPaths -> Status.UNHEALTHY_ROOT_ENTRY
                else -> Status.OK
            }
        }

        // todo needs tests
        fun resolveHealth(
            fsInteractor: FSInteractor,
            rootEntries: List<RootEntry>,
        ): HealthResponse {
            val rootEntriesHealth = rootEntries
                .map { RootEntryHealth.from(fsInteractor, it) }
            return HealthResponse(
                status = statusFromRootEntries(rootEntriesHealth),
                rootEntriesHealth = rootEntriesHealth,
            )
        }
    }
}
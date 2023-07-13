package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.RootEntry
import javax.persistence.*

@Entity(name = "root_entry")
class RootEntryEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        @Column(unique = true) val localId: String,
        @Column val path: String
) {

    fun toRootEntry(): RootEntry = RootEntry(
        id = localId,
        path = path,
    )

    companion object {
        fun from(rootEntry: RootEntry): RootEntryEntity {
            return RootEntryEntity(
                    localId = rootEntry.id,
                    path = rootEntry.path
            )
        }
    }
}

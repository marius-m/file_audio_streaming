package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.Track2
import javax.persistence.*

@Entity(name = "track")
class TrackEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        @Column val bookId: String,
        @Column(unique = true) val localId: String,
        @Column val rawName: String,
        @Column val title: String,
        @Column val titleEng: String,
        @Column val path: String
) {

    fun toTrack(): Track2 = Track2(
            bookId = bookId,
            id = localId,
            rawFileName = rawName,
            path = path
    )

    companion object {
        fun from(track: Track2): TrackEntity {
            return TrackEntity(
                    bookId = track.bookId,
                    localId = track.id,
                    rawName = track.rawFileName,
                    title = track.title,
                    titleEng = track.titleEng,
                    path = track.path
            )
        }
    }
}
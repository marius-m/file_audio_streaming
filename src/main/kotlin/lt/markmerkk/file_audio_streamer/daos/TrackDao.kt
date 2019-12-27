package lt.markmerkk.file_audio_streamer.daos

import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import lt.markmerkk.file_audio_streamer.models.jpa.TrackEntity
import org.springframework.data.repository.CrudRepository

interface TrackDao : CrudRepository<TrackEntity, Long> {
    fun findByLocalId(localId: String): TrackEntity?
    fun findByBookId(bookId: String): List<TrackEntity>
}
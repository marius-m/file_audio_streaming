package lt.markmerkk.file_audio_streamer.daos

import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import org.springframework.data.repository.CrudRepository

interface CategoryDao : CrudRepository<CategoryEntity, Long> {
    fun findByLocalId(localId: String): CategoryEntity?
}
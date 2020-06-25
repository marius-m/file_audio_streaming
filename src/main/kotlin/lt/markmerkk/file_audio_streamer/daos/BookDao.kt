package lt.markmerkk.file_audio_streamer.daos

import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import org.springframework.data.repository.CrudRepository

interface BookDao : CrudRepository<BookEntity, Long> {
    fun findByLocalId(localId: String): BookEntity?
    fun findByCategoryId(categoryId: String): List<BookEntity>
    fun findByTitleEngContains(titleEng: String): List<BookEntity>
}
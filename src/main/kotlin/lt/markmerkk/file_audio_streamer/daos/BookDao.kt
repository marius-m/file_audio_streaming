package lt.markmerkk.file_audio_streamer.daos

import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.time.OffsetDateTime

interface BookDao : CrudRepository<BookEntity, Long> {
    fun findByLocalId(localId: String): BookEntity?
    fun findByCategoryId(categoryId: String): List<BookEntity>
    fun findByTitleEngContains(titleEng: String): List<BookEntity>
    fun findByTitleEngContainsAndCategoryId(titleEng: String, categoryId: String): List<BookEntity>

    @Query("SELECT b FROM book b WHERE b.updatedAt > :instanceYearFromNow")
    fun findYearOldBooks(instanceYearFromNow: OffsetDateTime): List<BookEntity>
}
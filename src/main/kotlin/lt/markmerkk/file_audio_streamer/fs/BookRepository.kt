package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.RootEntryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.models.*
import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import org.slf4j.LoggerFactory

class BookRepository(
    private val fsInteractor: FSInteractor,
    private val fsSource: FSSource,
    private val uuidGen: UUIDGen,
    private val rootEntryDao: RootEntryDao,
    private val categoryDao: CategoryDao,
    private val bookDao: BookDao,
    private val trackDao: TrackDao,
    private val categoryCustomRepository: CategoryCustomRepository,
) {

    fun rootEntries(): List<RootEntry> {
        return rootEntryDao
            .findAll()
            .map { it.toRootEntry() }
    }

    fun categoriesFiles(): List<CategoryFile> {
        return categoryDao
            .findAll()
            .map { it.toCategory() }
    }

    fun categories(): List<Category> {
        return categoryCustomRepository.customCategories() + categoriesFiles()
    }

    @Throws(IllegalArgumentException::class)
    fun categoryBooks(categoryId: String): List<Book> {
        val categoryCustomType = CategoryCustomType.categoryTypeByName(name = categoryId)
        if (!categoryCustomType.isUndefined()) {
            return categoryCustomRepository.booksByCategory(categoryCustomType)
        }
        val categoryEntity = categoryDao.findByLocalId(categoryId) ?: throw IllegalArgumentException("No such category")
        return bookDao.findByCategoryId(categoryEntity.localId)
            .map { it.toBook() }
    }

    fun books(): List<Book> {
        return bookDao.findAll()
            .map { it.toBook() }
    }

    fun bookSearch(keyword: String): List<Book> {
        return bookDao.findByTitleEngContains(keyword.toLowerCase())
            .map { it.toBook() }
    }

    fun bookSearch(keyword: String, categoryId: String): List<Book> {
        l.info("Search for '${keyword}' / '${categoryId}'")
        return bookDao.findByTitleEngContainsAndCategoryId(keyword.toLowerCase(), categoryId)
            .map { it.toBook() }
    }

    @Throws(IllegalArgumentException::class)
    fun bookForId(bookId: String): BookEntity {
        return bookDao.findByLocalId(bookId) ?: throw IllegalArgumentException("No such book")
    }

    @Throws(IllegalArgumentException::class)
    fun tracksForBookId(bookId: String): List<Track> {
        val book = bookDao.findByLocalId(bookId) ?: throw IllegalArgumentException("No such book")
        return trackDao.findByBookId(bookId)
            .map { it.toTrack() }
    }

    @Throws(IllegalArgumentException::class)
    fun track(trackId: String): Track {
        return trackDao.findByLocalId(trackId)?.toTrack() ?: throw IllegalArgumentException("No such track")
    }

    companion object {
        private val l = LoggerFactory.getLogger(BookRepository::class.java)!!
        fun extractNameFromPath(path: String): String {
            return path.split("/").last()
        }
    }

}
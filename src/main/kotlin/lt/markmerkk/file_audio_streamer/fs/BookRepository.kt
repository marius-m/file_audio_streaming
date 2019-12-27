package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.models.Book2
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track2
import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import lt.markmerkk.file_audio_streamer.models.jpa.TrackEntity

class BookRepository(
        private val fsInteractor: FSInteractor,
        private val fsSource: FSSource,
        private val uuidGen: UUIDGen,
        private val categoryDao: CategoryDao,
        private val bookDao: BookDao,
        private val trackDao: TrackDao
) {

    fun renewCache() {
        categoryDao.deleteAll()
        bookDao.deleteAll()
        trackDao.deleteAll()
        val categories = initCategories(rootPathsWithDelimiter = fsSource.rootPathsWithDelimiter)
                .map { it.id to it }
                .toMap()
        val categoriesAsDaoObj = categories.values
                .map { CategoryEntity.from(it) }
        categoryDao.saveAll(categoriesAsDaoObj)
        val books = categories.values
                .flatMap { initBooksForCategory(it) }
                .map { it.id to it }
                .toMap()
        val booksAsDaoObj = books.values
                .map { BookEntity.from(it) }
        bookDao.saveAll(booksAsDaoObj)
        val tracks = books.values
                .flatMap { initTracksForBook(it) }
                .map { it.id to it }
                .toMap()
        val tracksAsDaoObj = tracks.values
                .map { TrackEntity.from(it) }
        trackDao.saveAll(tracksAsDaoObj)
    }

    fun categories(): List<Category> {
        return categoryDao
                .findAll()
                .map { it.toCategory() }
    }

    @Throws(IllegalArgumentException::class)
    fun categoryBooks(categoryId: String): List<Book2> {
        val category = categoryDao.findByLocalId(categoryId) ?: throw IllegalArgumentException("No such category")
        return bookDao.findByCategoryId(categoryId)
                .map { it.toBook() }
    }

    fun books(): List<Book2> {
        return bookDao.findAll()
                .map { it.toBook() }
    }

    @Throws(IllegalArgumentException::class)
    fun tracksForBook(bookId: String): List<Track2> {
        val book = bookDao.findByLocalId(bookId) ?: throw IllegalArgumentException("No such book")
        return trackDao.findByBookId(bookId)
                .map { it.toTrack() }
    }

    @Throws(IllegalArgumentException::class)
    fun track(trackId: String): Track2 {
        return trackDao.findByLocalId(trackId)?.toTrack() ?: throw IllegalArgumentException("No such track")
    }

    //region Convenience

    internal fun initCategories(
            rootPathsWithDelimiter: String
    ): List<Category> {
        if (rootPathsWithDelimiter.isEmpty()) {
            return emptyList()
        }
        val rootPaths = rootPathsWithDelimiter
                .split(",")
        return rootPaths
                .flatMap { fsInteractor.dirsInPath(it) }
                .map { it.absolutePath }
                .map { pathToCategory ->
                    val catName = extractNameFromPath(pathToCategory)
                    Category(id = uuidGen.genFrom(pathToCategory), title = catName, path = pathToCategory)
                }
    }

    internal fun initBooksForCategory(category: Category): List<Book2> {
        return fsInteractor.dirsInPath(category.path)
                .filter { it.isDirectory }
                .map { it.absolutePath }
                .map { pathToBook ->
                    val bookName = extractNameFromPath(pathToBook)
                    Book2(
                            categoryId = category.id,
                            id = uuidGen.genFrom(pathToBook),
                            title = bookName,
                            path = pathToBook
                    )
                }
    }

    internal fun initTracksForBook(book: Book2): List<Track2> {
        return fsInteractor.filesInPath(book.path)
                .map { trackAsFile ->
                    Track2(
                            bookId = book.id,
                            id = uuidGen.genFrom(trackAsFile.absolutePath),
                            rawFileName = trackAsFile.name,
                            path = trackAsFile.absolutePath
                    )
                }
                .filter { it.isSupported() }
    }

    //endregion

    companion object {
        fun extractNameFromPath(path: String): String {
            return path.split("/").last()
        }
    }

}
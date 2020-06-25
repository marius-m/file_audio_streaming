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
import org.slf4j.LoggerFactory

class BookRepository(
        private val fsInteractor: FSInteractor,
        private val fsSource: FSSource,
        private val uuidGen: UUIDGen,
        private val categoryDao: CategoryDao,
        private val bookDao: BookDao,
        private val trackDao: TrackDao
) {

    fun renewCache() {
        l.info("Renewing cache")
        categoryDao.deleteAll()
        bookDao.deleteAll()
        trackDao.deleteAll()
        l.info("Looking for categories...")
        val categories = initCategories(rootPathsWithDelimiter = fsSource.rootPathsWithDelimiter)
                .map { it.id to it }
                .toMap()
        val categoriesAsDaoObj = categories.values
                .map { CategoryEntity.from(it) }
        categoryDao.saveAll(categoriesAsDaoObj)
        l.info("Looking for books...")
        val books: Map<String, Book2> = categories.values
                .flatMap { initBooksForCategory(it) }
                .map { it.id to it }
                .toMap()
        val booksAsDaoObj = books.values
                .map { BookEntity.from(it) }
        bookDao.saveAll(booksAsDaoObj)
        l.info("Looking for tracks...")
        val tracks: Map<String, Track2> = books.values
                .flatMap { initTracksForBook(it) }
                .map { it.id to it }
                .toMap()
        val tracksAsDaoObj = tracks.values
                .map { TrackEntity.from(it) }
        trackDao.saveAll(tracksAsDaoObj)
        l.info("Removing books with empty tracks...")
        val emptyBooks = bookDao.findAll()
                .filter {
                    trackDao.findByBookId(it.localId)
                            .count() == 0
                }
        emptyBooks.forEach { book ->
                    l.info("Rm Book (${book.id} / ${book.title} / ${book.path}) as it has no tracks")
                    bookDao.delete(book)
                }
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
        l.info("Scanning for categories")
        val cateogries = rootPaths
                .flatMap {
                    l.info("Scanning categories in $it")
                    fsInteractor.dirsInPath(it)
                }
                .map { it.absolutePath }
                .map { pathToCategory ->
                    val catName = extractNameFromPath(pathToCategory)
                    val cat = Category(id = uuidGen.genFrom(pathToCategory), title = catName, path = pathToCategory)
                    l.info("Found category $cat")
                    cat
                }
        return cateogries
    }

    internal fun initBooksForCategory(category: Category): List<Book2> {
        l.info("Scanning books for Category(${category.title} / ${category.path})")
        val books = fsInteractor.dirsInPath(category.path)
                .filter { it.isDirectory }
                .map { it.absolutePath }
                .map { pathToBook ->
                    val bookName = extractNameFromPath(pathToBook)
                    val book = Book2(
                            categoryId = category.id,
                            id = uuidGen.genFrom(pathToBook),
                            title = bookName,
                            path = pathToBook
                    )
                    l.info("Found Book(${book.id} / ${book.title} / ${book.path})")
                    book
                }
        return books
    }

    internal fun initTracksForBook(book: Book2): List<Track2> {
        val tracks = fsInteractor.filesInPath(book.path)
                .map { trackAsFile ->
                    Track2(
                            bookId = book.id,
                            id = uuidGen.genFrom(trackAsFile.absolutePath),
                            rawFileName = trackAsFile.name,
                            path = trackAsFile.absolutePath
                    )
                }
                .filter { it.isSupported() }
        l.info("Found ${tracks.size} tracks for Book(${book.id} / ${book.title} / ${book.path})")
        return tracks
    }

    //endregion

    companion object {
        private val l = LoggerFactory.getLogger(BookRepository::class.java)!!
        fun extractNameFromPath(path: String): String {
            return path.split("/").last()
        }
    }

}
package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track
import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import lt.markmerkk.file_audio_streamer.models.jpa.TrackEntity
import org.apache.commons.lang3.time.StopWatch
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
        val sw = StopWatch().apply { start() }
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
        l.info("Category scan finish (${sw.time}ms)")
        l.info("Looking for books...")
        val books: Map<String, Book> = categories.values
                .flatMap { initBooksForCategory(it) }
                .map { it.id to it }
                .toMap()
        val booksAsDaoObj = books.values
                .map { BookEntity.from(it) }
        bookDao.saveAll(booksAsDaoObj)
        l.info("Book scan finish (${sw.time}ms)")
        l.info("Looking for tracks...")
        val tracks: Map<String, Track> = books.values
                .flatMap { initTracksForBook(it) }
                .map { it.id to it }
                .toMap()
        val tracksAsDaoObj = tracks.values
                .map { TrackEntity.from(it) }
        trackDao.saveAll(tracksAsDaoObj)
        l.info("Track scan finish (${sw.time}ms)")
        l.info("Removing books with empty tracks...")
        val emptyBooks = books.values
                .filter { book ->
                    val trackCountForBook = tracks
                            .filter { track -> track.value.bookId == book.id }
                            .count()
                    trackCountForBook == 0
                }
        emptyBooks
                .forEach { book ->
                    l.info("Rm Book (${book.id} / ${book.title} / ${book.path}) as it has no tracks")
                    val emptyBookEntity = bookDao.findByLocalId(book.id)
                    if (emptyBookEntity != null) {
                        bookDao.delete(emptyBookEntity)
                    }
                }
        sw.stop()
        l.info("Clean-up finish (${sw.time}ms)")
        l.info("Re-new finish (${sw.time}ms)")
    }

    fun categories(): List<Category> {
        return categoryDao
                .findAll()
                .map { it.toCategory() }
    }

    @Throws(IllegalArgumentException::class)
    fun categoryBooks(categoryId: String): List<Book> {
        val category = categoryDao.findByLocalId(categoryId) ?: throw IllegalArgumentException("No such category")
        return bookDao.findByCategoryId(categoryId)
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

    @Throws(IllegalArgumentException::class)
    fun tracksForBook(bookId: String): List<Track> {
        val book = bookDao.findByLocalId(bookId) ?: throw IllegalArgumentException("No such book")
        return trackDao.findByBookId(bookId)
                .map { it.toTrack() }
    }

    @Throws(IllegalArgumentException::class)
    fun track(trackId: String): Track {
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
                    val cat = Category(
                            id = uuidGen.genFrom(pathToCategory),
                            title = catName,
                            path = pathToCategory
                    )
                    l.info("Found category $cat")
                    cat
                }
        return cateogries
    }

    internal fun initBooksForCategory(category: Category): List<Book> {
        l.info("Scanning books for Category(${category.title} / ${category.path})")
        val books = fsInteractor.dirsInPath(category.path)
                .filter { it.isDirectory }
                .map { it.absolutePath }
                .map { pathToBook ->
                    val bookName = extractNameFromPath(pathToBook)
                    val book = Book(
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

    internal fun initTracksForBook(book: Book): List<Track> {
        val tracks = fsInteractor.filesInPath(book.path)
                .map { trackAsFile ->
                    Track(
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
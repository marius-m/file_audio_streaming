package lt.markmerkk.file_audio_streamer.fs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.fs.entities.IndexStats
import lt.markmerkk.file_audio_streamer.fs.entities.IndexStatus
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track
import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import lt.markmerkk.file_audio_streamer.models.jpa.TrackEntity
import org.apache.commons.lang3.time.StopWatch
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PreDestroy

/**
 * Responsible for renewing file index
 */
class FileIndexer(
    private val fsInteractor: FSInteractor,
    private val fsSource: FSSource,
    private val uuidGen: UUIDGen,
    private val categoryDao: CategoryDao,
    private val bookDao: BookDao,
    private val trackDao: TrackDao
) {

    private var sw = StopWatch()
    private val isRunning = AtomicBoolean(false)
    private var scopeIo = CoroutineScope(Dispatchers.IO)
    private var indexStats = IndexStats.asEmpty()

    fun renewIndex() {
        if (isRunning.get()) {
            scopeIo.cancel()
            scopeIo = CoroutineScope(Dispatchers.IO)
        }
        scopeIo.launch {
            try {
                sw = StopWatch().apply { start() }
                isRunning.set(true)
                l.info("Renewing cache")
                internalRenewCache(sw)
                sw.stop()
                l.info("Clean-up finish (${sw.time}ms)")
                l.info("Re-new finish (${sw.time}ms)")
            } catch (e: Exception) {
                l.error("Error renewing cache", e)
            } finally {
                isRunning.set(false)
            }
        }
    }

    fun indexStatus() = IndexStatus.build(
        indexStats,
        isRunning.get(),
        sw,
    )

    @PreDestroy
    fun destroy() {
        scopeIo.cancel()
    }

    private suspend fun internalRenewCache(sw: StopWatch) {
        indexStats = IndexStats.asEmpty()
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
        indexStats = IndexStats.withCats(
            categoryCount = categories.size
        )
        val books: Map<String, Book> = categories.values
            .flatMap { initBooksForCategory(it) }
            .map { it.id to it }
            .toMap()
        val booksAsDaoObj = books.values
            .map { BookEntity.from(it) }
        bookDao.saveAll(booksAsDaoObj)
        l.info("Book scan finish (${sw.time}ms)")
        l.info("Looking for tracks...")
        indexStats = IndexStats.withCatsBooks(
            categoryCount = categories.size,
            bookCount = books.size,
        )
        val tracks: Map<String, Track> = books.values
            .flatMap { initTracksForBook(it) }
            .map { it.id to it }
            .toMap()
        val tracksAsDaoObj = tracks.values
            .map { TrackEntity.from(it) }
        trackDao.saveAll(tracksAsDaoObj)
        l.info("Track scan finish (${sw.time}ms)")
        indexStats = IndexStats.withCatsBooksTracks(
            categoryCount = categories.size,
            bookCount = books.size,
            trackCount = tracks.size,
        )
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
        indexStats = IndexStats.withCatsBooksTracksEB(
            categoryCount = categories.size,
            bookCount = books.size,
            trackCount = tracks.size,
            emptyBookCount = emptyBooks.size,
        )
    }

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
                val catName = BookRepository.extractNameFromPath(pathToCategory)
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
                val bookName = BookRepository.extractNameFromPath(pathToBook)
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

    companion object {
        private val l = LoggerFactory.getLogger(FileIndexer::class.java)!!
    }

}
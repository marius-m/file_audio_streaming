package lt.markmerkk.file_audio_streamer.fs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.RootEntryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.fs.entities.IndexStats
import lt.markmerkk.file_audio_streamer.fs.entities.IndexStatus
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.RootEntry
import lt.markmerkk.file_audio_streamer.models.Track
import lt.markmerkk.file_audio_streamer.models.jpa.BookEntity
import lt.markmerkk.file_audio_streamer.models.jpa.CategoryEntity
import lt.markmerkk.file_audio_streamer.models.jpa.RootEntryEntity
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
    private val rootEntryDao: RootEntryDao,
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
        val isBuilder = IndexStats.IndexStatsBuilder()
        indexStats = isBuilder.build()
        rootEntryDao.deleteAll()
        categoryDao.deleteAll()
        bookDao.deleteAll()
        trackDao.deleteAll()
        l.info("Mapping root entries...")
        val rootEntries = initRootEntries(rootPathsWithDelimiter = fsSource.rootPathsWithDelimiter)
        rootEntryDao.saveAll(RootEntryEntity.from(rootEntries))
        indexStats = isBuilder
            .appendRootEntries(rootEntries.size)
            .build()
        l.info("Looking for categories...")
        val categories = initCategories(rootEntries)
            .map { it.id to it }
            .toMap()
        val categoriesAsDaoObj = categories.values
            .map { CategoryEntity.from(it) }
        categoryDao.saveAll(categoriesAsDaoObj)
        l.info("Category scan finish (${sw.time}ms)")
        l.info("Looking for books...")
        indexStats = isBuilder
            .appendCategories(categories.size)
            .build()
        val books: Map<String, Book> = categories.values
            .flatMap { initBooksForCategory(it) }
            .map { it.id to it }
            .toMap()
        val booksAsDaoObj = books.values
            .map { BookEntity.from(it) }
        bookDao.saveAll(booksAsDaoObj)
        l.info("Book scan finish (${sw.time}ms)")
        l.info("Looking for tracks...")
        indexStats = isBuilder.appendBooks(books.size).build()
        val tracks: Map<String, Track> = books.values
            .flatMap { initTracksForBook(it) }
            .map { it.id to it }
            .toMap()
        val tracksAsDaoObj = tracks.values
            .map { TrackEntity.from(it) }
        trackDao.saveAll(tracksAsDaoObj)
        l.info("Track scan finish (${sw.time}ms)")
        indexStats = isBuilder
            .appendTracks(tracks.size)
            .build()
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
        indexStats = isBuilder
            .appendEmptyBooks(emptyBooks.size)
            .build()
    }

    internal fun initRootEntries(
        rootPathsWithDelimiter: String
    ): List<RootEntry> {
        if (rootPathsWithDelimiter.isEmpty()) {
            return emptyList()
        }
        val rootPaths = rootPathsWithDelimiter
            .split(",")
        l.info("Scanning for root entries")
        val rootEntries = rootPaths
            .mapNotNull { rootPath ->
                l.info("Resolving root entry ($rootPath)")
                fsInteractor.resolvePathAsHealthyDirectoryOrNull(rootPath)
            }
            .map { existingDirectory ->
                val rootEntry = RootEntry(
                    id = uuidGen.genFrom(existingDirectory.absolutePath),
                    path = existingDirectory.absolutePath
                )
                l.info("Found RootEntry $rootEntry")
                rootEntry
            }
        return rootEntries
    }

    internal fun initCategories(
        rootEntries: List<RootEntry>,
    ): List<Category> {
        if (rootEntries.isEmpty()) {
            return emptyList()
        }
        l.info("Scanning for categories")
        return rootEntries
            .map { rootEntry ->
                l.info("Scanning categories in $rootEntry")
                rootEntry to fsInteractor.dirsInPath(rootEntry.path)
            }
            .flatMap { (rootEntry, categoryDirs) ->
                val categories = categoryDirs.map { categoryDir ->
                    val categoryDirPath = categoryDir.absolutePath
                    val catName = BookRepository.extractNameFromPath(categoryDirPath)
                    val cat = Category(
                        rootEntryId = rootEntry.id,
                        id = uuidGen.genFrom(categoryDirPath),
                        title = catName,
                        path = categoryDirPath
                    )
                    l.info("Found category $cat")
                    cat
                }
                categories
            }
    }

    internal fun initBooksForCategory(category: Category): List<Book> {
        l.info("Scanning books for Category(${category.title} / ${category.path})")
        val dirsInPath = fsInteractor.dirsInPath(category.path)
        val dirsInPathAsString = dirsInPath
            .map { it.absolutePath }
        l.debug("Found raw books: ${dirsInPathAsString}")
        val books = dirsInPath
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
        val filesInPath = fsInteractor.filesInPath(book.path)
        val filesInPathAsString = filesInPath.map { it.absolutePath }
        l.debug("Found raw tracks (${filesInPath.size}) for Book(${book.id} / ${book.title} / ${book.path}): ${filesInPathAsString}")
        val tracks = filesInPath
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
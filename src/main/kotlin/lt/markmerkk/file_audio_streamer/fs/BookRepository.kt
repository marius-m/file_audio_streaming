package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Book2
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track2

class BookRepository(
        private val fsInteractor: FSInteractor,
        private val fsSource: FSSource,
        private val uuidGen: UUIDGen
) {

    // category_id by category
    private var categories: Map<String, Category> = emptyMap()
    // book_id by category
    private var books: Map<String, Book2> = emptyMap()
    // track_id by track
    private var tracks: Map<String, Track2> = emptyMap()

    fun renewCache() {
        this.categories = initCategories(rootPathsWithDelimiter = fsSource.rootPathsWithDelimiter)
                .map { it.id to it }
                .toMap()
        this.books = categories.values
                .flatMap { initBooksForCategory(it) }
                .map { it.id to it }
                .toMap()
        this.tracks = books.values
                .flatMap { initTracksForBook(it) }
                .map { it.id to it }
                .toMap()
    }

    fun categories(): List<Category> = categories.values.toList()

    @Throws(IllegalArgumentException::class)
    fun categoryBooks(categoryId: String): List<Book2> {
        val category = categories[categoryId] ?: throw IllegalArgumentException("No such category")
        return books.values
                .filter { it.categoryId == category.id }
    }

    fun books(): List<Book2> {
        return this
                .books
                .values
                .toList()
    }

    @Throws(IllegalArgumentException::class)
    fun tracksForBook(bookId: String): List<Track2> {
        val book = books[bookId] ?: throw IllegalArgumentException("No such book")
        return tracks
                .values
                .filter { it.bookId == book.id }
    }

    @Throws(IllegalArgumentException::class)
    fun track(trackId: String): Track2 {
        return tracks[trackId] ?: throw IllegalArgumentException("No such track")
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
                    Category(id = uuidGen.generate(), title = catName, path = pathToCategory)
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
                            id = uuidGen.generate(),
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
                            id = uuidGen.generate(),
                            rawFileName = trackAsFile.name,
                            path = trackAsFile.absolutePath
                    )
                }
                .filter { it.isSupported() }
    }

    //endregion

    companion object {
        fun tracksPathForBook(rootPath: String, book: Book): String = "$rootPath/${book.title}"
        fun extractNameFromPath(path: String): String {
            return path.split("/").last()
        }
    }

}
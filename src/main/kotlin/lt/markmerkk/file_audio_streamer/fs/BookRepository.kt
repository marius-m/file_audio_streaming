package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Book2
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track

class BookRepository(
        private val fsInteractor: FSInteractor,
        private val fsSource: FSSource,
        private val uuidGen: UUIDGen
) {

    // category_id by category
    private var categories: Map<String, Category> = emptyMap()
    // book_id by category
    private var books: Map<String, Book2> = emptyMap()

    fun renewCache() {
        this.categories = initCategories(rootPathsWithDelimiter = fsSource.rootPathsWithDelimiter)
                .map { it.id to it }
                .toMap()
        this.books = categories.values
                .flatMap { initBooksForCategory(it) }
                .map { it.id to it }
                .toMap()
    }

    fun categories(): List<Category> = categories.values.toList()

    fun bookAtIndex(index: Int): Book? = books().getOrNull(index)

    fun books(): List<Book> {
        return fsInteractor.filesInPath("${fsSource.rootPath}/")
                .filter { it.isDirectory }
                .filter { it.exists() }
                .mapIndexed { index, file ->  Book.from(index, file) }
    }

    fun trackAtIndex(book: Book, index: Int): Track? = tracksForBook(book).getOrNull(index)

    fun tracksForBook(book: Book): List<Track> {
        val tracksPathForBook = tracksPathForBook(fsSource.rootPath, book)
        return fsInteractor.filesInPath(tracksPathForBook)
                .filter { it.isFile }
                .filter { it.exists() }
                .mapIndexed { index, file ->  Track.from(book.index, index, file) }
                .filter { it.isSupported() }
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

    //endregion

    companion object {
        fun tracksPathForBook(rootPath: String, book: Book): String = "$rootPath/${book.title}"
        fun extractNameFromPath(path: String): String {
            return path.split("/").last()
        }
    }

}
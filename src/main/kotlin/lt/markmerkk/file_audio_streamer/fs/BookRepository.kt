package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track

class BookRepository(
        private val fsInteractor: FSInteractor,
        private val fsSource: FSSource
) {

    private var categories: List<Category> = initCategories(fsSource.categoriesAsArgs)

    fun categories(): List<Category> = categories

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

    companion object {
        fun tracksPathForBook(rootPath: String, book: Book): String = "$rootPath/${book.title}"
        fun initCategories(
                categoriesAsArgs: String
        ): List<Category> {
            if (categoriesAsArgs.isEmpty()) {
                return emptyList()
            }
            return categoriesAsArgs
                    .split(",")
                    .mapIndexed { index, path ->
                        val catName = extractCategoryName(path)
                        Category(index = index, title = catName, path = path)
                    }
        }

        fun extractCategoryName(path: String): String {
            return path.split("/").last()
        }
    }

}
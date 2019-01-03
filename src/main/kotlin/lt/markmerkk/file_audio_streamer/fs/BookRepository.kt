package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Track

class BookRepository(
        private val fsInteractor: FSInteractor
) {

    fun bookAtIndex(index: Int): Book? = books().getOrNull(index)

    fun books(): List<Book> {
        return fsInteractor.filesInPath("$ROOT_PATH/")
                .filter { it.isDirectory }
                .filter { it.exists() }
                .mapIndexed { index, file ->  Book.from(index, file) }
    }

    fun trackAtIndex(book: Book, index: Int): Track? = tracksForBook(book).getOrNull(index)

    fun tracksForBook(book: Book): List<Track> {
        val tracksPathForBook = tracksPathForBook(book)
        return fsInteractor.filesInPath(tracksPathForBook)
                .filter { it.isFile }
                .filter { it.exists() }
                .mapIndexed { index, file ->  Track.from(book.index, index, file) }
                .filter { it.isSupported() }
    }

    companion object {
        const val ROOT_PATH = "books"
        fun tracksPathForBook(book: Book): String = "$ROOT_PATH/${book.title}"
    }

}
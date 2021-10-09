package lt.markmerkk.file_audio_streamer.fs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class FileIndexerInitTracksTest {

    @Mock lateinit var fsInteractor: FSInteractor
    @Mock lateinit var fsSource: FSSource
    @Mock lateinit var uuidGen: UUIDGen
    @Mock lateinit var categoryDao: CategoryDao
    @Mock lateinit var booksDao: BookDao
    @Mock lateinit var tracksDao: TrackDao

    private lateinit var fileIndexer: FileIndexer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        fileIndexer = FileIndexer(
            fsInteractor = fsInteractor,
            fsSource = fsSource,
            uuidGen = uuidGen,
            categoryDao = categoryDao,
            bookDao = booksDao,
            trackDao = tracksDao
        )
    }

    @Test
    fun valid() {
        // Assemble
        val book1 = Mocks.createBook(id = "b_id1", path = "/root/books/book1")
        val trackPath1 = Mocks.mockFile(
                isFile = true,
                name = "book1.mp3",
                absolutePath = "/root/books/book1/book1.mp3"
        )
        doReturn("t_id1").whenever(uuidGen).genFrom(any())
        doReturn(listOf(trackPath1))
                .whenever(fsInteractor).filesInPath("/root/books/book1")

        // Act
        val result = fileIndexer.initTracksForBook(book1)

        // Assert
        val track = result[0]
        assertThat(track.bookId).isEqualTo("b_id1")
        assertThat(track.id).isEqualTo("t_id1")
        assertThat(track.rawFileName).isEqualTo("book1.mp3")
        assertThat(track.path).isEqualTo("/root/books/book1/book1.mp3")
    }

    @Test
    fun directoryInBookPath() {
        // Assemble
        val book1 = Mocks.createBook(id = "b_id1", path = "/root/books/book1")
        val trackPath1 = Mocks.mockFile(
                isFile = false,
                name = "book1",
                absolutePath = "/root/books/book1/book1"
        )
        doReturn("t_id1").whenever(uuidGen).genFrom(any())
        doReturn(listOf(trackPath1)) // should not happen
                .whenever(fsInteractor).filesInPath("/root/books/book1")

        // Act
        val result = fileIndexer.initTracksForBook(book1)

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun unsupportedTrackType() {
        // Assemble
        val book1 = Mocks.createBook(id = "b_id1", path = "/root/books/book1")
        val trackPath1 = Mocks.mockFile(
                isFile = true,
                name = "book1.jpg",
                absolutePath = "/root/books/book1/book1.jpg"
        )
        doReturn("t_id1").whenever(uuidGen).genFrom(any())
        doReturn(listOf(trackPath1))
                .whenever(fsInteractor).filesInPath("/root/books/book1")

        // Act
        val result = fileIndexer.initTracksForBook(book1)

        // Assert
        assertThat(result).isEmpty()
    }

}
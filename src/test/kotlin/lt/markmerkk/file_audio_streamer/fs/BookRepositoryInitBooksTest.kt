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

class BookRepositoryInitBooksTest {

    @Mock lateinit var fsInteractor: FSInteractor
    @Mock lateinit var fsSource: FSSource
    @Mock lateinit var uuidGen: UUIDGen
    @Mock lateinit var categoryDao: CategoryDao
    @Mock lateinit var booksDao: BookDao
    @Mock lateinit var tracksDao: TrackDao
    lateinit var bookRepository: BookRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        bookRepository = BookRepository(
                fsInteractor = fsInteractor,
                fsSource = fsSource,
                uuidGen = uuidGen,
                categoryDao = categoryDao,
                bookDao = booksDao,
                trackDao = tracksDao
        )
    }

    @Test
    fun validBooks() {
        // Assemble
        val category1 = Mocks.createCategory(
                id = "c_id1",
                path = "/root/books"
        )
        doReturn("b_id1").whenever(uuidGen).genFrom(any())
        val bookPath1 = Mocks.mockFile(isDirectory = true, absolutePath = "/root/books/book1")
        doReturn(listOf(bookPath1))
                .whenever(fsInteractor).dirsInPath("/root/books")

        // Act
        val result = bookRepository.initBooksForCategory(category1)

        // Assert
        val book1 = result[0]
        assertThat(book1.categoryId).isEqualTo("c_id1")
        assertThat(book1.id).isEqualTo("b_id1")
        assertThat(book1.title).isEqualTo("book1")
        assertThat(book1.path).isEqualTo("/root/books/book1")
    }

    @Test
    fun fileInDirectory() {
        // Assemble
        val category1 = Mocks.createCategory(
                id = "c_id1",
                path = "/root/books"
        )
        doReturn("b_id1").whenever(uuidGen).genFrom(any())
        val bookPath1 = Mocks.mockFile(isDirectory = false, absolutePath = "/root/books/book1.mp3")
        doReturn(listOf(bookPath1))
                .whenever(fsInteractor).dirsInPath("/root/books") // should not happen

        // Act
        val result = bookRepository.initBooksForCategory(category1)

        // Assert
        assertThat(result).isEmpty()
    }

}
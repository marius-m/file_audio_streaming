package lt.markmerkk.file_audio_streamer.fs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.models.Book
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class BookRepositoryBookAtIndexTest {

    @Mock
    lateinit var fsInteractor: FSInteractor
    lateinit var bookRepository: BookRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        bookRepository = BookRepository(
                fsInteractor = fsInteractor
        )
    }

    @Test
    fun noBooks() {
        // Assemble
        doReturn(emptyList<File>()).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBook = bookRepository.bookAtIndex(0)

        // Assert
        assertThat(resultBook).isNull()
    }

    @Test
    fun validBook() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(name = "book1")
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBook = bookRepository.bookAtIndex(0)

        // Assert
        assertThat(resultBook).isEqualTo(Book(index = 0, title = "book1"))
    }

    @Test
    fun indexTooLow() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(name = "book1")
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBook = bookRepository.bookAtIndex(-1)

        // Assert
        assertThat(resultBook).isNull()
    }

    @Test
    fun indexTooHigh() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(name = "book1")
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBook = bookRepository.bookAtIndex(2)

        // Assert
        assertThat(resultBook).isNull()
    }

}
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

class BookRepositoryBooksTest {

    @Mock lateinit var fsInteractor: FSInteractor
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
        val resultBooks = bookRepository.books()

        // Assert
        assertThat(resultBooks).isEmpty()
    }

    @Test
    fun validBook() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(
                exists = true,
                name = "book1"
        )
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBooks = bookRepository.books()

        // Assert
        assertThat(resultBooks).containsExactly(
                Book(index = 0, title = "book1")
        )
    }

    @Test
    fun validBooks() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(
                exists = true,
                name = "book1"
        )
        val bookFile2 = Mocks.mockFileAsDirectory(
                exists = true,
                name = "book2"
        )
        doReturn(listOf(bookFile1, bookFile2)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBooks = bookRepository.books()

        // Assert
        assertThat(resultBooks).containsExactly(
                Book(index = 0, title = "book1"),
                Book(index = 1, title = "book2")
        )
    }

    @Test
    fun notExistingPaths() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsDirectory(
                exists = false,
                name = "book1"
        )
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBooks = bookRepository.books()

        // Assert
        assertThat(resultBooks).isEmpty()
    }

    @Test
    fun filesInBookDirectory() {
        // Assemble
        val bookFile1 = Mocks.mockFileAsFile(
                exists = true,
                name = "book1"
        )
        doReturn(listOf(bookFile1)).whenever(fsInteractor).filesInPath(any())

        // Act
        val resultBooks = bookRepository.books()

        // Assert
        assertThat(resultBooks).isEmpty()
    }

}
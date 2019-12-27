package lt.markmerkk.file_audio_streamer.fs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.models.Category
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class BookRepositoryInitCategoriesTest {

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
    fun noCategories() {
        // Act
        val result = bookRepository.initCategories(rootPathsWithDelimiter = "")

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun oneCategory() {
        // Assemble
        doReturn("id1").whenever(uuidGen).genFrom(any())
        doReturn(listOf(Mocks.mockFile(absolutePath = "/root/books/book1")))
                .whenever(fsInteractor).dirsInPath("/root/books")

        // Act
        val result = bookRepository.initCategories(rootPathsWithDelimiter = "/root/books")

        // Assert
        assertThat(result).containsExactly(
                Category(id = "id1", title = "book1", path = "/root/books/book1")
        )
    }

    @Test
    fun multiCatInOneSource() {
        // Assemble
        val categories = listOf(
                Mocks.mockFile(absolutePath = "/root/books/book1"),
                Mocks.mockFile(absolutePath = "/root/books/book2"),
                Mocks.mockFile(absolutePath = "/root/books/book3")
        )
        doReturn("id1")
                .doReturn("id2")
                .doReturn("id3")
                .whenever(uuidGen).genFrom(any())
        doReturn(categories)
                .whenever(fsInteractor).dirsInPath("/root/books")

        // Act
        val result = bookRepository.initCategories(rootPathsWithDelimiter = "/root/books")

        // Assert
        assertThat(result).containsExactly(
                Category(id = "id1", title = "book1", path = "/root/books/book1"),
                Category(id = "id2", title = "book2", path = "/root/books/book2"),
                Category(id = "id3", title = "book3", path = "/root/books/book3")
        )
    }

    @Test
    fun multipleSources() {
        // Assemble
        doReturn("id1")
                .doReturn("id2")
                .doReturn("id3")
                .whenever(uuidGen).genFrom(any())
        doReturn(listOf(Mocks.mockFile(absolutePath = "/root/books/book1")))
                .whenever(fsInteractor).dirsInPath("/root/books")
        doReturn(listOf(Mocks.mockFile(absolutePath = "/root/books2/book1")))
                .whenever(fsInteractor).dirsInPath("/root/books2")

        // Act
        val result = bookRepository.initCategories(
                rootPathsWithDelimiter = "/root/books,/root/books2"
        )

        // Assert
        assertThat(result).containsExactly(
                Category(id = "id1", title = "book1", path = "/root/books/book1"),
                Category(id = "id2", title = "book1", path = "/root/books2/book1")
        )
    }
}
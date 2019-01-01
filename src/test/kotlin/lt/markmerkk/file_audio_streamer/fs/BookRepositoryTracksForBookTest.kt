package lt.markmerkk.file_audio_streamer.fs

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Track
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File

class BookRepositoryTracksForBookTest {

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
    fun noTracks() {
        // Assemble
        val book1 = Book(index = 0, title = "book1")
        val bookPath = BookRepository.tracksPathForBook(book1)
        doReturn(emptyList<File>()).whenever(fsInteractor).filesInPath(bookPath)

        // Act
        val resultTracks = bookRepository.tracksForBook(book1)

        // Assert
        assertThat(resultTracks).isEmpty()
    }

    @Test
    fun validTrack() {
        // Assemble
        val book1 = Book(index = 0, title = "book1")
        val bookPath = BookRepository.tracksPathForBook(book1)
        val tracks = listOf(Mocks.mockFileAsFile(name = "track1.mp3"))
        doReturn(tracks).whenever(fsInteractor).filesInPath(bookPath)

        // Act
        val resultTracks = bookRepository.tracksForBook(book1)

        // Assert
        assertThat(resultTracks).containsExactly(
                Track(
                        index = 0,
                        rawTitle = "track1.mp3",
                        path = "valid_path"
                )
        )
    }

    @Test
    fun validTracks() {
        // Assemble
        val book1 = Book(index = 0, title = "book1")
        val bookPath = BookRepository.tracksPathForBook(book1)
        val tracks = listOf(
                Mocks.mockFileAsFile(name = "track1.mp3"),
                Mocks.mockFileAsFile(name = "track2.mp3"),
                Mocks.mockFileAsFile(name = "track3.mp3")
        )
        doReturn(tracks).whenever(fsInteractor).filesInPath(bookPath)

        // Act
        val resultTracks = bookRepository.tracksForBook(book1)

        // Assert
        assertThat(resultTracks).containsExactly(
                Track(
                        index = 0,
                        rawTitle = "track1.mp3",
                        path = "valid_path"
                ),
                Track(
                        index = 1,
                        rawTitle = "track2.mp3",
                        path = "valid_path"
                ),
                Track(
                        index = 2,
                        rawTitle = "track3.mp3",
                        path = "valid_path"
                )
        )
    }

    @Test
    fun directoryInTrackPath() {
        // Assemble
        val book1 = Book(index = 0, title = "book1")
        val bookPath = BookRepository.tracksPathForBook(book1)
        val tracks = listOf(
                Mocks.mockFileAsDirectory(name = "subdirectory")
        )
        doReturn(tracks).whenever(fsInteractor).filesInPath(bookPath)

        // Act
        val resultTracks = bookRepository.tracksForBook(book1)

        // Assert
        assertThat(resultTracks).isEmpty()
    }

    @Test
    fun fileDoesNotExist() {
        // Assemble
        val book1 = Book(index = 0, title = "book1")
        val bookPath = BookRepository.tracksPathForBook(book1)
        val tracks = listOf(Mocks.mockFileAsFile(exists = false, name = "track1.mp3"))
        doReturn(tracks).whenever(fsInteractor).filesInPath(bookPath)

        // Act
        val resultTracks = bookRepository.tracksForBook(book1)

        // Assert
        assertThat(resultTracks).isEmpty()
    }

}
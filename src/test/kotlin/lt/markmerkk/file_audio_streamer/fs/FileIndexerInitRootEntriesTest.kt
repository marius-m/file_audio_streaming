package lt.markmerkk.file_audio_streamer.fs

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.RootEntryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.models.RootEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class FileIndexerInitRootEntriesTest {

    @Mock lateinit var fsInteractor: FSInteractor
    @Mock lateinit var fsSource: FSSource
    @Mock lateinit var uuidGen: UUIDGen
    @Mock lateinit var rootEntryDao: RootEntryDao
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
            rootEntryDao = rootEntryDao,
            categoryDao = categoryDao,
            bookDao = booksDao,
            trackDao = tracksDao
        )
    }

    @Test
    fun noEntries() {
        // Act
        val result = fileIndexer.initRootEntries(rootPathsWithDelimiter = "")

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun oneEntry() {
        // Assemble
        doReturn("id1").whenever(uuidGen).genFrom(any())
        doReturn(Mocks.mockFileAsDirectory(exists = true, name = "books1"))
                .whenever(fsInteractor).resolvePathAsHealthyDirectoryOrNull("/root/books")

        // Act
        val result = fileIndexer.initRootEntries(rootPathsWithDelimiter = "/root/books")

        // Assert
        assertThat(result).containsExactly(
                RootEntry(id = "id1", path = "/root/books1")
        )
    }

    @Test
    fun multipleSources() {
        // Assemble
        doReturn("id1").whenever(uuidGen).genFrom("/root/books1")
        doReturn("id2").whenever(uuidGen).genFrom("/root/books2")
        doReturn(Mocks.mockFileAsDirectory(exists = true, name = "books1"))
            .whenever(fsInteractor).resolvePathAsHealthyDirectoryOrNull("/root/books1")
        doReturn(Mocks.mockFileAsDirectory(exists = true, name = "books2"))
            .whenever(fsInteractor).resolvePathAsHealthyDirectoryOrNull("/root/books2")

        // Act
        val result = fileIndexer.initRootEntries(
                rootPathsWithDelimiter = "/root/books1,/root/books2"
        )

        // Assert
        assertThat(result).containsExactly(
                RootEntry(id = "id1", path = "/root/books1"),
                RootEntry(id = "id2", path = "/root/books2")
        )
    }

    @Test
    fun multipleSources_nonExistingPath() {
        // Assemble
        doReturn("id1").whenever(uuidGen).genFrom("/root/books1")
        doReturn("id2").whenever(uuidGen).genFrom("/root/books2")
        doReturn(Mocks.mockFileAsDirectory(exists = true, name = "books1"))
            .whenever(fsInteractor).resolvePathAsHealthyDirectoryOrNull("/root/books1")
        doReturn(null)
            .whenever(fsInteractor).resolvePathAsHealthyDirectoryOrNull("/root/books2")

        // Act
        val result = fileIndexer.initRootEntries(
            rootPathsWithDelimiter = "/root/books1,/root/books2"
        )

        // Assert
        assertThat(result).containsExactly(
            RootEntry(id = "id1", path = "/root/books1"),
        )
    }
}

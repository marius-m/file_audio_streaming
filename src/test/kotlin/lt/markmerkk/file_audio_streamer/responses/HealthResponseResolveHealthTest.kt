package lt.markmerkk.file_audio_streamer.responses

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HealthResponseResolveHealthTest {

    @Mock lateinit var fsInteractor: FSInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun noRoot() {
        // Assemble
        // Act
        val result = HealthResponse.resolveHealth(
            fsInteractor = fsInteractor,
            rootEntries = emptyList()
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse(
                status = HealthResponse.Status.OK,
                rootEntriesHealth = emptyList(),
            )
        )
    }

    @Test
    fun oneRootEntry_valid() {
        // Assemble
        val rootEntry1 = Mocks.createRootEntry(id = "root1", path = "/root/books")
        val rootEntries = listOf(
            rootEntry1,
        )
        doReturn(Mocks.mockFile(exists = true))
            .whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(rootEntry1.path)

        // Act
        val result = HealthResponse.resolveHealth(
            fsInteractor = fsInteractor,
            rootEntries = rootEntries,
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse(
                status = HealthResponse.Status.OK,
                rootEntriesHealth = listOf(
                    HealthResponse.RootEntryHealth(
                        rootEntry = rootEntry1,
                        health = HealthResponse.FileHealth(exists = true),
                    )
                )
            )
        )
    }

    @Test
    fun multipleRootEntries_valid() {
        // Assemble
        val rootEntry1 = Mocks.createRootEntry(id = "root1", path = "/root/books1")
        val rootEntry2 = Mocks.createRootEntry(id = "root1", path = "/root/books2")
        val rootEntries = listOf(
            rootEntry1,
            rootEntry2,
        )
        doReturn(Mocks.mockFile(exists = true))
            .whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(rootEntry1.path)
        doReturn(Mocks.mockFile(exists = true))
            .whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(rootEntry2.path)

        // Act
        val result = HealthResponse.resolveHealth(
            fsInteractor = fsInteractor,
            rootEntries = rootEntries,
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse(
                status = HealthResponse.Status.OK,
                rootEntriesHealth = listOf(
                    HealthResponse.RootEntryHealth(
                        rootEntry = rootEntry1,
                        health = HealthResponse.FileHealth(exists = true),
                    ),
                    HealthResponse.RootEntryHealth(
                        rootEntry = rootEntry2,
                        health = HealthResponse.FileHealth(exists = true),
                    ),
                )
            )
        )
    }

    @Test
    fun multipleRootEntries_oneFailing() {
        // Assemble
        val rootEntry1 = Mocks.createRootEntry(id = "root1", path = "/root/books1")
        val rootEntry2 = Mocks.createRootEntry(id = "root2", path = "/root/books2")
        val rootEntries = listOf(
            rootEntry1,
            rootEntry2,
        )
        doReturn(Mocks.mockFile(exists = true))
            .whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(rootEntry1.path)
        doReturn(null)
            .whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(rootEntry2.path)

        // Act
        val result = HealthResponse.resolveHealth(
            fsInteractor = fsInteractor,
            rootEntries = rootEntries,
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse(
                status = HealthResponse.Status.UNHEALTHY_ROOT_ENTRY,
                rootEntriesHealth = listOf(
                    HealthResponse.RootEntryHealth(
                        rootEntry = rootEntry1,
                        health = HealthResponse.FileHealth(exists = true),
                    ),
                    HealthResponse.RootEntryHealth(
                        rootEntry = rootEntry2,
                        health = HealthResponse.FileHealth(exists = false),
                    ),
                )
            )
        )
    }
}
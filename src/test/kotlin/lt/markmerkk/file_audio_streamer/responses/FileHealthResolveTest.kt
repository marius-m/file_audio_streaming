package lt.markmerkk.file_audio_streamer.responses

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.Mocks
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class FileHealthResolveTest {

    @Mock lateinit var fsInteractor: FSInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun valid() {
        // Assemble
        val filePath = "/root/book1"
        doReturn(Mocks.mockFile(exists = true)).whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(filePath)

        // Act
        val result = HealthResponse.FileHealth.from(
            fsInteractor = fsInteractor,
            filePath = filePath,
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse.FileHealth(exists = true)
        )
    }

    @Test
    fun doesNotExist() {
        // Assemble
        val filePath = "/root/book1"
        doReturn(null).whenever(fsInteractor)
            .resolvePathAsHealthyDirectoryOrNull(filePath)

        // Act
        val result = HealthResponse.FileHealth.from(
            fsInteractor = fsInteractor,
            filePath = filePath,
        )

        // Assert
        assertThat(result).isEqualTo(
            HealthResponse.FileHealth(exists = false)
        )
    }
}
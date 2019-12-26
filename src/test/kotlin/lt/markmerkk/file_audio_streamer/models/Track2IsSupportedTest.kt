package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.Mocks
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class Track2IsSupportedTest {

    @Test
    fun supportedType() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "title.mp3")
                .isSupported()

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun supportedType_upperCase() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "TITLE.MP3")
                .isSupported()

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun unsupported_mixed() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "titmp3le.png")
                .isSupported()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun unknownType() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "title")
                .isSupported()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun wrongType() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "title.png")
                .isSupported()

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun metadataFile() {
        // Assemble
        // Act
        val result = Mocks.createTrack2(rawTitle = "._title.mp3")
                .isSupported()

        // Assert
        assertThat(result).isFalse()
    }

}
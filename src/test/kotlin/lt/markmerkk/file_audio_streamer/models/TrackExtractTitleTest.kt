package lt.markmerkk.file_audio_streamer.models

import lt.markmerkk.file_audio_streamer.Mocks
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TrackExtractTitleTest {

    @Test
    fun extractMp3_1() {
        // Assemble
        // Act
        val resultTitle = Mocks.createTrack(rawTitle = "title.mp3")
                .extractTitle()

        // Assert
        assertThat(resultTitle).isEqualTo("title")
    }

    @Test
    fun extractMp3_2() {
        // Assemble
        // Act
        val resultTitle = Mocks.createTrack(rawTitle = "TITLE.MP3")
                .extractTitle()

        // Assert
        assertThat(resultTitle).isEqualTo("TITLE")
    }

    @Test
    fun extractWav_1() {
        // Assemble
        // Act
        val resultTitle = Mocks.createTrack(rawTitle = "title.wav")
                .extractTitle()

        // Assert
        assertThat(resultTitle).isEqualTo("title")
    }

    @Test
    fun extractWav_2() {
        // Assemble
        // Act
        val resultTitle = Mocks.createTrack(rawTitle = "TITLE.WAV")
                .extractTitle()

        // Assert
        assertThat(resultTitle).isEqualTo("TITLE")
    }

}
package lt.markmerkk.file_audio_streamer

import java.util.*

class UUIDGen {
    fun generate(): String {
        return UUID.randomUUID()
                .toString()
    }
}

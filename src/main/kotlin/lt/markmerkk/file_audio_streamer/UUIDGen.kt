package lt.markmerkk.file_audio_streamer

import org.apache.commons.codec.digest.DigestUtils

class UUIDGen {

    fun genFrom(filePath: String): String {
        return DigestUtils.md5Hex(filePath)
    }

}

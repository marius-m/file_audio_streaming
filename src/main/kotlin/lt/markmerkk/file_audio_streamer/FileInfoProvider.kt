package lt.markmerkk.file_audio_streamer

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

interface FileInfoProvider {
    fun readBasicAttributes(file: File): BasicFileAttributes
}

class FileInfoProviderImpl: FileInfoProvider {
    override fun readBasicAttributes(file: File): BasicFileAttributes {
        val path = Paths.get(file.absolutePath)
        val attr: BasicFileAttributes = Files
            .readAttributes(path, BasicFileAttributes::class.java)
        return attr
    }
}

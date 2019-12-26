package lt.markmerkk.file_audio_streamer.fs

import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import java.io.File

class FSInteractor(
        private val resourceLoader: ResourceLoader
) {

    fun dirsInPath(path: String): List<File> {
        val rootFile: File = ResourcePatternUtils
                .getResourcePatternResolver(resourceLoader)
                .getResource("file:$path")
                .file
        if (rootFile.exists() && rootFile.isDirectory) {
            return rootFile.listFiles()
                    .filter { it.isDirectory }
        }
        return emptyList()
    }

    fun filesInPath(path: String): List<File> {
        val rootFile: File = ResourcePatternUtils
                .getResourcePatternResolver(resourceLoader)
                .getResource("file:$path")
                .file
        if (rootFile.exists() && rootFile.isDirectory) {
            return rootFile.listFiles().toList()
                    .filter { it.isFile }
        }
        return listOf(rootFile)
    }

    fun fileAsResource(absoluteFilePath: String): Resource = ResourcePatternUtils
            .getResourcePatternResolver(resourceLoader)
            .getResource("file:$absoluteFilePath")
}
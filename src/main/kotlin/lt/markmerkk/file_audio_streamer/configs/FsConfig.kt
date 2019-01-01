package lt.markmerkk.file_audio_streamer.configs

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import org.springframework.content.fs.config.EnableFilesystemStores
import org.springframework.content.fs.io.FileSystemResourceLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.io.File
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry




@Configuration
@EnableFilesystemStores
class FsConfig {

    fun filesystemRoot(): File {
        return File("/Users/mariusmerkevicius/Projects/personal/audio_streamer/books")
    }

    @Bean
    open fun fsResourceLoader(): FileSystemResourceLoader {
        return FileSystemResourceLoader(filesystemRoot().absolutePath)
    }

    @Bean
    open fun fsInteractor(
            resourceLoader: ResourceLoader
    ): FSInteractor {
        return FSInteractor(resourceLoader)
    }

    @Bean
    open fun bookRepository(
            fsInteractor: FSInteractor
    ): BookRepository {
        return BookRepository(fsInteractor)
    }

}
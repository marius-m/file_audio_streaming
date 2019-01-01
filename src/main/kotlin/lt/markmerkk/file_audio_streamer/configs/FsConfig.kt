package lt.markmerkk.file_audio_streamer.configs

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader


@Configuration
class FsConfig {

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
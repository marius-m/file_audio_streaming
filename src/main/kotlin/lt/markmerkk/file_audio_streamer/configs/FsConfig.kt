package lt.markmerkk.file_audio_streamer.configs

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.fs.FSSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader


@Configuration
@PropertySource("classpath:creds.properties", ignoreResourceNotFound = true)
class FsConfig {

    @Bean
    open fun provideCredentials(
            env: Environment
    ): Credentials {
        return Credentials(
                env.getProperty("basic_username"),
                env.getProperty("basic_password")
        )
    }

    @Bean
    open fun provideFsConfig(
            @Value("\${rootPath}") rootConfig: String
    ): FSSource {
        return FSSource(rootConfig)
    }

    @Bean
    open fun fsInteractor(
            resourceLoader: ResourceLoader
    ): FSInteractor {
        return FSInteractor(resourceLoader)
    }

    @Bean
    open fun bookRepository(
            fsInteractor: FSInteractor,
            fsSource: FSSource
    ): BookRepository {
        return BookRepository(fsInteractor, fsSource)
    }

}
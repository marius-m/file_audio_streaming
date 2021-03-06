package lt.markmerkk.file_audio_streamer.configs

import io.sentry.Sentry
import io.sentry.SentryClient
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.fs.FSSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader


@Configuration
@PropertySource("classpath:creds.properties", ignoreResourceNotFound = true)
class ComponentsConfig {

    @Bean
    open fun provideUUID(
            env: Environment
    ): UUIDGen {
        return UUIDGen()
    }

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
            @Value("\${rootPaths}") rootPaths: String
    ): FSSource {
        return FSSource(rootPaths)
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
            fsSource: FSSource,
            uuidGen: UUIDGen,
            categoryDao: CategoryDao,
            bookDao: BookDao,
            trackDao: TrackDao
    ): BookRepository {
        return BookRepository(
                fsInteractor,
                fsSource,
                uuidGen,
                categoryDao,
                bookDao,
                trackDao
        ).apply { renewCache() }
    }

    @Bean
    @Profile("prod")
    open fun sentryClientProd(): SentryClient {
        return Sentry.init("https://948d91b168824f27a0490a1484b692c3@o348125.ingest.sentry.io/5289085")
    }

    @Bean
    @Profile("dev")
    open fun sentryClientDev(): SentryClient {
        return Sentry.init()
    }

}
package lt.markmerkk.file_audio_streamer.configs

import io.sentry.Sentry
import io.sentry.SentryClient
import lt.markmerkk.file_audio_streamer.BuildConfig
import lt.markmerkk.file_audio_streamer.UUIDGen
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.daos.CategoryDao
import lt.markmerkk.file_audio_streamer.daos.RootEntryDao
import lt.markmerkk.file_audio_streamer.daos.TrackDao
import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.fs.FSSource
import lt.markmerkk.file_audio_streamer.fs.FileIndexer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader

@Configuration
@PropertySource("classpath:creds.properties", ignoreResourceNotFound = true)
class ComponentsConfig {

    @Bean
    @Scope("singleton")
    open fun provideBuildConfig(
        @Value("#{servletContext.contextPath}") contextPath: String
    ): BuildConfig {
        val buildConfig = BuildConfig.build(
            contextPath = contextPath
        )
        return buildConfig
    }

    @Bean
    @Scope("singleton")
    open fun provideUUID(
        env: Environment
    ): UUIDGen {
        return UUIDGen()
    }

    @Bean
    @Scope("singleton")
    open fun provideCredentials(
        env: Environment
    ): Credentials {
        return Credentials(
            env.getProperty("basic_username"),
            env.getProperty("basic_password")
        )
    }

    @Bean
    @Scope("singleton")
    open fun provideFsConfig(
        @Value("\${rootPaths}") rootPaths: String
    ): FSSource {
        return FSSource(rootPaths)
    }

    @Bean
    @Scope("singleton")
    open fun fsInteractor(
        resourceLoader: ResourceLoader
    ): FSInteractor {
        return FSInteractor(resourceLoader)
    }

    @Bean
    @Scope("singleton")
    open fun bookRepository(
        fsInteractor: FSInteractor,
        fsSource: FSSource,
        uuidGen: UUIDGen,
        rootEntryDao: RootEntryDao,
        categoryDao: CategoryDao,
        bookDao: BookDao,
        trackDao: TrackDao
    ): BookRepository {
        return BookRepository(
            fsInteractor,
            fsSource,
            uuidGen,
            rootEntryDao,
            categoryDao,
            bookDao,
            trackDao
        )
    }

    @Bean
    @Scope("singleton")
    open fun fileIndexer(
        fsInteractor: FSInteractor,
        fsSource: FSSource,
        uuidGen: UUIDGen,
        rootEntryDao: RootEntryDao,
        categoryDao: CategoryDao,
        bookDao: BookDao,
        trackDao: TrackDao
    ): FileIndexer {
        return FileIndexer(
            fsInteractor,
            fsSource,
            uuidGen,
            rootEntryDao,
            categoryDao,
            bookDao,
            trackDao
        ).apply { renewIndex() }
    }

    @Bean
    @Profile("prod")
    @Scope("singleton")
    open fun sentryClientProd(): Unit {
        return Sentry.init("https://948d91b168824f27a0490a1484b692c3@o348125.ingest.sentry.io/5289085")
    }

    @Bean
    @Profile("dev")
    @Scope("singleton")
    open fun sentryClientDev(): Unit {
        return Sentry.init()
    }
}
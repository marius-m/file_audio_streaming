package lt.markmerkk

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.FileInfoProvider
import java.io.File
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

object FileInfoProviderTest: FileInfoProvider {

    val timeProvider: TimeProvider = TimeProviderTest

    override fun readBasicAttributes(file: File): BasicFileAttributes {
        val fileTime: FileTime = mock()
        doReturn(timeProvider.now().toInstant()).whenever(fileTime).toInstant()
        val fileAttrs: BasicFileAttributes = mock()
        doReturn(fileTime).whenever(fileAttrs).lastModifiedTime()
        doReturn(fileTime).whenever(fileAttrs).creationTime()
        return fileAttrs
    }
}
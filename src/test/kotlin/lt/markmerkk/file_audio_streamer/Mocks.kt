package lt.markmerkk.file_audio_streamer

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.models.Track
import java.io.File

object Mocks {

    fun createTrack(
            bookIndex: Int = 0,
            index: Int = 0,
            rawTitle: String = "valid_title_$index.mp3",
            path: String = "valid_path"
    ): Track = Track(
            bookIndex = bookIndex,
            rawFileName = rawTitle,
            index = index,
            path = path
    )

    //region Mocks

    fun mockFile(
            exists: Boolean = true,
            isDirectory: Boolean = false,
            isFile: Boolean = true,
            name: String = "valid_file",
            absolutePath: String = "valid_path"
    ): File {
        val file: File = mock()
        doReturn(exists).whenever(file).exists()
        doReturn(isDirectory).whenever(file).isDirectory
        doReturn(isFile).whenever(file).isFile
        doReturn(name).whenever(file).name
        doReturn(absolutePath).whenever(file).absolutePath
        return file
    }

    fun mockFileAsFile(
            exists: Boolean = true,
            name: String = "valid_file"
    ): File = mockFile(
            exists = exists,
            isDirectory = false,
            isFile = true,
            name = name
    )

    fun mockFileAsDirectory(
            exists: Boolean = true,
            name: String = "valid_dir"
    ): File = mockFile(
            exists = exists,
            isDirectory = true,
            isFile = false,
            name = name
    )

    //endregion

}
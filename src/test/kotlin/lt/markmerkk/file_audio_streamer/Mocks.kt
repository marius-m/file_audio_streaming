package lt.markmerkk.file_audio_streamer

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import lt.markmerkk.file_audio_streamer.models.Book2
import lt.markmerkk.file_audio_streamer.models.Category
import lt.markmerkk.file_audio_streamer.models.Track2
import java.io.File

object Mocks {

    fun createCategory(
            id: String = "id1",
            title: String = "books",
            path: String = "/root/books"
    ): Category = Category(
            id = id,
            title = title,
            path = path
    )

    fun createTrack2(
            bookId: String = "id1",
            id: String = "id1",
            rawTitle: String = "book1.mp3",
            path: String = "/root/books/book1.mp3"
    ): Track2 = Track2(
            bookId = bookId,
            id = id,
            rawFileName = rawTitle,
            path = path
    )

    fun createBook(
            categoryId: String = "c_id1",
            id: String = "b_id1",
            title: String = "books",
            path: String = "/root/books"
    ): Book2 = Book2(
            categoryId = categoryId,
            id = id,
            title = title,
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
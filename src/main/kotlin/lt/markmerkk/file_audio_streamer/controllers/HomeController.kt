package lt.markmerkk.file_audio_streamer.controllers

import lt.markmerkk.file_audio_streamer.Consts
import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.Track
import lt.markmerkk.file_audio_streamer.responses.BookResponse
import lt.markmerkk.file_audio_streamer.responses.TrackResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/")
class HomeController {

    @Autowired lateinit var bookRepository: BookRepository
    @Autowired lateinit var fsInteractor: FSInteractor

    @RequestMapping(
            value = ["/${Consts.ENDPOINT_BOOKS}"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun books(): List<BookResponse> {
        return bookRepository.books()
                .map { BookResponse.from(it) }
    }

    @RequestMapping(
            value = ["/${Consts.ENDPOINT_BOOKS}/{bookIndex}"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun bookTracks(
            @PathVariable bookIndex: Int
    ): List<TrackResponse> {
        val book = bookRepository.bookAtIndex(bookIndex) ?: throw BookNotFoundException()
        return bookRepository.tracksForBook(book)
                .map { TrackResponse.from(it) }
    }

    @RequestMapping(
            value = ["/${Consts.ENDPOINT_BOOKS}/{bookIndex}/${Consts.ENDPOINT_TRACKS}/{trackIndex}"],
            method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun mapTrack(
            @PathVariable bookIndex: Int,
            @PathVariable trackIndex: Int
    ): Resource {
        val book = bookRepository.bookAtIndex(bookIndex) ?: throw BookNotFoundException()
        val tracksForBook = bookRepository.tracksForBook(book)
        val track = tracksForBook.getOrNull(trackIndex) ?: throw TrackNotFoundException()
        return fsInteractor.fileAsResource(track.path)
    }

    //region Classes

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class BookNotFoundException: IllegalArgumentException()

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class TrackNotFoundException: IllegalArgumentException()

    //endregion

}
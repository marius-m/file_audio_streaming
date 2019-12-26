package lt.markmerkk.file_audio_streamer.controllers

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FSInteractor
import lt.markmerkk.file_audio_streamer.responses.BookResponse
import lt.markmerkk.file_audio_streamer.responses.CategoryResponse
import lt.markmerkk.file_audio_streamer.responses.TrackResponse
import lt.markmerkk.utils.MultipartFileSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api/")
class HomeController {

    @Autowired lateinit var bookRepository: BookRepository
    @Autowired lateinit var fsInteractor: FSInteractor

    @RequestMapping(
            value = ["/categories"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun categories(): List<CategoryResponse> {
        return bookRepository.categories()
                .map { CategoryResponse.from(it) }
    }

    @RequestMapping(
            value = ["/categories/{categoryIndex}/books"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun books(
            @PathVariable categoryIndex: Int
    ): List<BookResponse> {
        TODO()
    }

    @RequestMapping(
            value = ["/books/{bookIndex}"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun bookTracks(
            @PathVariable bookIndex: Int
    ): List<TrackResponse> {
        val book = bookRepository.bookAtIndex(bookIndex) ?: throw BookNotFoundException()
        return bookRepository.tracksForBook(book)
                .map { TrackResponse.from(book, it) }
    }

    @RequestMapping(
            value = ["/books/{bookIndex}/categories/{trackIndex}"],
            method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun mapTrack(
            @PathVariable bookIndex: Int,
            @PathVariable trackIndex: Int,
            response: HttpServletResponse,
            request: HttpServletRequest
    ) {
        val book = bookRepository.bookAtIndex(bookIndex) ?: throw BookNotFoundException()
        val tracksForBook = bookRepository.tracksForBook(book)
        val track = tracksForBook.getOrNull(trackIndex) ?: throw TrackNotFoundException()

        val headers = HttpHeaders()
        headers.cacheControl = CacheControl.noCache().headerValue
        return MultipartFileSender.fromFile(File(track.path))
                .with(response)
                .with(request)
                .serveResource()
    }

    //region Classes

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class CategoryNotFoundException: IllegalArgumentException()

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class BookNotFoundException: IllegalArgumentException()

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class TrackNotFoundException: IllegalArgumentException()

    //endregion

}
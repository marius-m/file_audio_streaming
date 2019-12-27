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
class ApiController {

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
            value = ["/categories/{categoryId}/books"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun booksForCategory(
            @PathVariable categoryId: String
    ): List<BookResponse> {
        return bookRepository.categoryBooks(categoryId)
                .map { BookResponse.from(it) }
    }

    @RequestMapping(
            value = ["/books"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun allBooks(
    ): List<BookResponse> {
        return bookRepository.books()
                .map { BookResponse.from(it) }
    }

    @RequestMapping(
            value = ["/books/{bookId}"],
            method = [RequestMethod.GET],
            produces = ["application/json"]
    )
    @ResponseBody
    fun bookTracks(
            @PathVariable bookId: String
    ): List<TrackResponse> {
        return bookRepository.tracksForBook(bookId)
                .map { TrackResponse.from(it) }
    }

    @RequestMapping(
            value = ["/tracks/{trackId}"],
            method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun track(
            @PathVariable trackId: String,
            response: HttpServletResponse,
            request: HttpServletRequest
    ) {
        val track = bookRepository.track(trackId)
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
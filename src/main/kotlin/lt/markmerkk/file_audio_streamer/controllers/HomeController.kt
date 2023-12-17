package lt.markmerkk.file_audio_streamer.controllers

import lt.markmerkk.file_audio_streamer.BuildConfig
import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.fs.FileIndexer
import lt.markmerkk.file_audio_streamer.models.form.FormEntitySearch
import lt.markmerkk.file_audio_streamer.models.web.NavItem
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class HomeController(
    @Autowired val bookRepository: BookRepository,
    @Autowired val bc: BuildConfig,
    @Autowired val fileIndexer: FileIndexer,
) {

//    @RequestMapping(
//        value = ["/customError"],
//        method = [RequestMethod.GET]
//    )
//    fun renderError(
//        model: Model
//    ): String {
//        error("Test error")
//    }

    @RequestMapping(
            value = ["/categories"],
            method = [RequestMethod.GET]
    )
    fun renderCategories(
            model: Model
    ): String {
        val navItems = listOf(NavItem.asRoot(bc), NavItem.asCategories(bc))
        model.addAttribute("navItems", navItems)
        val categories = bookRepository
                .categories()
                .sortedByDescending { it.updatedAt }
        model.addAttribute("categories", categories)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "categories"
    }

    @RequestMapping(
            value = ["/categories/{categoryId}/books"],
            method = [RequestMethod.GET]
    )
    fun renderCategoryBooks(
            model: Model,
            @PathVariable categoryId: String,
            @ModelAttribute formEntitySearch: FormEntitySearch
    ): String {
        val navItems = listOf(
                NavItem.asRoot(bc),
                NavItem.asCategories(bc),
                NavItem.asCategoryBooks(bc, categoryId)
        )
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryId", categoryId)
        val keyword = formEntitySearch.keyword
        val books = if (keyword.isNullOrEmpty()) {
            bookRepository
                    .categoryBooks(categoryId)
                    .sortedByDescending { it.updatedAt }
        } else {
            bookRepository
                    .bookSearch(keyword, categoryId)
                    .sortedByDescending { it.updatedAt }
        }
        model.addAttribute("books", books)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "cat_books"
    }

    @RequestMapping(
            value = ["/books"],
            method = [RequestMethod.GET]
    )
    fun renderAllBooks(
            model: Model,
            @ModelAttribute formEntitySearch: FormEntitySearch
    ): String {
        val navItems = listOf(
                NavItem.asRoot(bc),
                NavItem.asCategories(bc),
                NavItem.asBooks(bc)
        )
        val keyword = formEntitySearch.keyword
        val books = if (keyword.isNullOrEmpty()) {
            bookRepository
                    .books()
                    .sortedByDescending { it.updatedAt }
        } else {
            bookRepository
                    .bookSearch(keyword)
                    .sortedByDescending { it.updatedAt }
        }
        model.addAttribute("navItems", navItems)
        model.addAttribute("books", books)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "books"
    }

    @RequestMapping(
        value = ["/books/year"],
        method = [RequestMethod.GET]
    )
    fun renderBooksYearOld(
        model: Model,
    ): String {
        val navItems = listOf(
            NavItem.asRoot(bc),
            NavItem.asCategories(bc),
            NavItem.asBooks(bc)
        )
        val now = DateTimeUtils.now()
        val yearFromNow = now.minusYears(1)
        val books = bookRepository
            .booksOlderThan(instance = yearFromNow)
            .sortedByDescending { it.updatedAt }
        model.addAttribute("metaYearFrom", DateTimeUtils.formatToStringAsBasic(yearFromNow))
        model.addAttribute("navItems", navItems)
        model.addAttribute("books", books)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "books-year"
    }

    @RequestMapping(
            value = ["/categories/{categoryId}/books/{bookId}"],
            method = [RequestMethod.GET]
    )
    fun renderTracksForCategoryBook(
            model: Model,
            @PathVariable categoryId: String,
            @PathVariable bookId: String
    ): String {
        val navItems = listOf(
                NavItem.asRoot(bc),
                NavItem.asCategories(bc),
                NavItem.asCategoryBooks(bc, categoryId),
                NavItem.asBook(bc, bookId)
        )
        val book = bookRepository.bookForId(bookId)
        val tracksForBook = bookRepository
            .tracksForBookId(bookId)
            .sortedBy { it.title }
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("bookId", bookId)
        model.addAttribute("bookPath", book.path)
        model.addAttribute("tracks", tracksForBook)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "tracks"
    }

    @RequestMapping(
            value = ["/books/{bookId}"],
            method = [RequestMethod.GET]
    )
    fun renderTracksForBook(
            model: Model,
            @PathVariable bookId: String
    ): String {

        val navItems = listOf(
                NavItem.asRoot(bc),
                NavItem.asCategories(bc),
                NavItem.asBook(bc, bookId)
        )
        val book = bookRepository.bookForId(bookId)
        val tracksForBook = bookRepository
            .tracksForBookId(bookId)
            .sortedBy { it.title }
        model.addAttribute("navItems", navItems)
        model.addAttribute("bookId", bookId)
        model.addAttribute("bookPath", book.path)
        model.addAttribute("tracks", tracksForBook)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "tracks"
    }

    @RequestMapping(
            value = ["/","/index"],
            method = [RequestMethod.GET]
    )
    fun renderIndex(
            model: Model
    ): String {
        val navItems = listOf(NavItem.asRoot(bc), NavItem.asCategories(bc))
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryCount", bookRepository.categories().size)
        model.addAttribute("bookCount", bookRepository.books().size)
        model.addAttribute("indexStatus", fileIndexer.indexStatus())
        return "index"
    }

    @RequestMapping(
            value = ["/re-index"],
            method = [RequestMethod.GET]
    )
    fun renderReIndex(
            model: Model
    ): String {
        l.info("Re-indexing folders...")
        fileIndexer.renewIndex()
        return "redirect:/"
    }

    companion object {
        private val l = LoggerFactory.getLogger(HomeController::class.java)!!
    }

}
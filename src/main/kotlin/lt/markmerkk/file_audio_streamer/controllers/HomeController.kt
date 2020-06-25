package lt.markmerkk.file_audio_streamer.controllers

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.models.web.NavItem
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class HomeController(
        @Autowired val bookRepository: BookRepository
) {

    @RequestMapping(
            value = ["/categories"],
            method = [RequestMethod.GET]
    )
    fun renderCategories(
            model: Model
    ): String {
        val navItems = listOf(NavItem.asRoot(), NavItem.asCategories().makeActive())
        model.addAttribute("navItems", navItems)
        model.addAttribute("categories", bookRepository.categories())
        return "categories"
    }

    @RequestMapping(
            value = ["/categories/{categoryId}/books"],
            method = [RequestMethod.GET]
    )
    fun renderCategoryBooks(
            model: Model,
            @PathVariable categoryId: String
    ): String {
        val navItems = listOf(
                NavItem.asRoot(),
                NavItem.asCategories(),
                NavItem.asCategoryBooks(categoryId).makeActive()
        )
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("books", bookRepository.categoryBooks(categoryId))
        return "cat_books"
    }

    @RequestMapping(
            value = ["/books"],
            method = [RequestMethod.GET]
    )
    fun renderAllBooks(
            model: Model
    ): String {
        val navItems = listOf(
                NavItem.asRoot(),
                NavItem.asCategories(),
                NavItem.asBooks().makeActive()
        )
        model.addAttribute("navItems", navItems)
        model.addAttribute("books", bookRepository.books())
        return "books"
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
                NavItem.asRoot(),
                NavItem.asCategories(),
                NavItem.asCategoryBooks(categoryId),
                NavItem.asBook(bookId).makeActive()
        )
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("bookId", bookId)
        model.addAttribute("tracks", bookRepository.tracksForBook(bookId))
        return "tracks"
    }

    @RequestMapping(
            value = ["/","/index"],
            method = [RequestMethod.GET]
    )
    fun renderIndex(
            model: Model
    ): String {
        val navItems = listOf(NavItem.asRoot(), NavItem.asCategories())
        model.addAttribute("navItems", navItems)
        model.addAttribute("categoryCount", bookRepository.categories().size)
        model.addAttribute("bookCount", bookRepository.books().size)
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
        bookRepository.renewCache()
        l.info("File index complete!")
        return "redirect:/index"
    }

    companion object {
        private val l = LoggerFactory.getLogger(HomeController::class.java)!!
    }

}
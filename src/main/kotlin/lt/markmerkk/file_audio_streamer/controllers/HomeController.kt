package lt.markmerkk.file_audio_streamer.controllers

import lt.markmerkk.file_audio_streamer.fs.BookRepository
import lt.markmerkk.file_audio_streamer.models.web.NavItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@Profile("dev")
class HomeController(
        @Autowired val bookRepository: BookRepository
) {

    @RequestMapping(
            value = ["/", "/categories"],
            method = [RequestMethod.GET]
    )
    fun renderIndex(
            model: Model
    ): String {
        val navItems = listOf(NavItem.asRoot().makeActive())
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
        val navItems = listOf(NavItem.asRoot(), NavItem.asCategoryBooks(categoryId).makeActive())
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
        val navItems = listOf(NavItem.asRoot(), NavItem.asBooks().makeActive())
        model.addAttribute("navItems", navItems)
        model.addAttribute("books", bookRepository.books())
        return "books"
    }

    @RequestMapping(
            value = ["/books/{bookId}"],
            method = [RequestMethod.GET]
    )
    fun renderTracksForBook(
            model: Model,
            @PathVariable bookId: String
    ): String {
        val navItems = listOf(NavItem.asRoot(), NavItem.asBooks(), NavItem.asBook(bookId).makeActive())
        model.addAttribute("navItems", navItems)
        model.addAttribute("bookId", bookId)
        model.addAttribute("tracks", bookRepository.tracksForBook(bookId))
        return "tracks"
    }

}
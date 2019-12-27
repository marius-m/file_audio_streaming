package lt.markmerkk.file_audio_streamer.models.web

data class NavItem(
        val title: String,
        val path: String,
        val isActive: Boolean = false
) {
    companion object {
        fun asRoot(): NavItem = NavItem(title = "Categories", path = "/")
        fun asCategoryBooks(categoryId: String): NavItem = NavItem(title = "Books", path = "/categories/$categoryId/books")
        fun asBooks(): NavItem = NavItem(title = "Books", path = "/books")
        fun asBook(bookId: String): NavItem = NavItem(title = "Book", path = "/books/$bookId")
    }
}
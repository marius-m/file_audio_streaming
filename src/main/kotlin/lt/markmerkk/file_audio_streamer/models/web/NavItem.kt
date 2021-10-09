package lt.markmerkk.file_audio_streamer.models.web

import lt.markmerkk.file_audio_streamer.BuildConfig

data class NavItem(
    val title: String,
    val path: String,
    val active: Boolean = false
) {

    fun makeActive(): NavItem = NavItem(title = title, path = path, active = true)

    companion object {
        fun asRoot(buildConfig: BuildConfig): NavItem = NavItem(
            title = "Home",
            path = "${buildConfig.contextPath}/"
        )
        fun asCategories(buildConfig: BuildConfig): NavItem = NavItem(
            title = "Categories",
            path = "${buildConfig.contextPath}/categories"
        )
        fun asCategoryBooks(
            buildConfig: BuildConfig,
            categoryId: String
        ): NavItem = NavItem(
            title = "Books",
            path = "${buildConfig.contextPath}/categories/$categoryId/books"
        )
        fun asBooks(buildConfig: BuildConfig): NavItem = NavItem(
            title = "Books",
            path = "${buildConfig.contextPath}/books"
        )
        fun asBook(
            buildConfig: BuildConfig,
            bookId: String
        ): NavItem = NavItem(
            title = "Book",
            path = "${buildConfig.contextPath}/books/$bookId"
        )
    }
}
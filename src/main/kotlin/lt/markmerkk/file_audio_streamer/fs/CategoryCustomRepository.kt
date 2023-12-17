package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.TimeProvider
import lt.markmerkk.file_audio_streamer.daos.BookDao
import lt.markmerkk.file_audio_streamer.models.Book
import lt.markmerkk.file_audio_streamer.models.CategoryCustom
import lt.markmerkk.file_audio_streamer.models.CategoryCustomType
import java.time.OffsetDateTime

class CategoryCustomRepository(
    private val timeProvider: TimeProvider,
    private val bookDao: BookDao,
) {
    private val categoriesCustom: Map<CategoryCustomType, CategoryCustom> = setOf(
        CategoryCustom(
            _id = CategoryCustomType.YEAR_1.name,
            _title = "Books in 1 year",
            type = CategoryCustomType.YEAR_1,
        ),
        CategoryCustom(
            _id = CategoryCustomType.HALF_YEAR.name,
            _title = "Books in half a year",
            type = CategoryCustomType.HALF_YEAR,
        ),
    ).map { it.type to it }
        .toMap()

    fun customCategories(): List<CategoryCustom> {
        return categoriesCustom.values.toList()
    }

    fun isCustomCategoryById(categoryId: String): Boolean {
        return CategoryCustomType.categoryTypeByName(name = categoryId) != CategoryCustomType.UNDEFINED
    }

    fun booksByCategory(categoryCustomType: CategoryCustomType): List<Book> {
        val now = timeProvider.now()
        return when (categoryCustomType) {
            CategoryCustomType.UNDEFINED -> emptyList()
            CategoryCustomType.YEAR_1 -> booksOlderThan(now.minusYears(1))
            CategoryCustomType.HALF_YEAR -> booksOlderThan(now.minusMonths(6))
        }
    }

    fun booksOlderThan(instance: OffsetDateTime): List<Book> {
        return bookDao.findYearOldBooks(instance)
            .map { it.toBook() }
    }
}

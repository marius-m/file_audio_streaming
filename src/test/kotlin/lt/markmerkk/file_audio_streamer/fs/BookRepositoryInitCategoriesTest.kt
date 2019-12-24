package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.models.Category
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class BookRepositoryInitCategoriesTest {

    @Mock lateinit var fsInteractor: FSInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun noCategories() {
        // Act
        val result = BookRepository.initCategories(categoriesAsArgs = "")

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun relativePath() {
        // Act
        val result = BookRepository.initCategories(
                categoriesAsArgs = "cat1"
        )

        // Assert
        assertThat(result).containsExactly(
                Category(index = 0, title = "cat1", path = "cat1")
        )
    }

    @Test
    fun oneSource() {
        // Act
        val result = BookRepository.initCategories(
                categoriesAsArgs = "/books/cat1"
        )

        // Assert
        assertThat(result).containsExactly(
                Category(index = 0, title = "cat1", path = "/books/cat1")
        )
    }

    @Test
    fun multipleSource() {
        // Act
        val result = BookRepository.initCategories(
                categoriesAsArgs = "/books/cat1,/books/cat2"
        )

        // Assert
        assertThat(result).containsExactly(
                Category(index = 0, title = "cat1", path = "/books/cat1"),
                Category(index = 1, title = "cat2", path = "/books/cat2")
        )
    }
}
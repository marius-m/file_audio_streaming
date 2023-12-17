package lt.markmerkk.file_audio_streamer.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CategoryCustomTypeByNameTest {
    @Test
    fun byValidName() {
        val categoryName = "YEAR_1"
        val result = CategoryCustomType.categoryTypeByName(categoryName)
        assertThat(result).isEqualTo(CategoryCustomType.YEAR_1)
    }

    @Test
    fun byValidName_lowercase() {
        val categoryName = "year_1"
        val result = CategoryCustomType.categoryTypeByName(categoryName)
        assertThat(result).isEqualTo(CategoryCustomType.YEAR_1)
    }

    @Test
    fun invalidName() {
        val categoryName = "invalid"
        val result = CategoryCustomType.categoryTypeByName(categoryName)
        assertThat(result).isEqualTo(CategoryCustomType.UNDEFINED)
    }

    @Test
    fun empty() {
        val categoryName = ""
        val result = CategoryCustomType.categoryTypeByName(categoryName)
        assertThat(result).isEqualTo(CategoryCustomType.UNDEFINED)
    }
}

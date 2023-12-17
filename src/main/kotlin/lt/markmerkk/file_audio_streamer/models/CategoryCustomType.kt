package lt.markmerkk.file_audio_streamer.models

enum class CategoryCustomType {
    UNDEFINED,
    YEAR_1, // Books updated / created in less than 1 year
    HALF_YEAR, // Books updated / created in half a 1 year
    ;

    fun isUndefined(): Boolean = this == UNDEFINED

    companion object {
        fun categoryTypeByName(name: String): CategoryCustomType {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: UNDEFINED
        }
    }
}
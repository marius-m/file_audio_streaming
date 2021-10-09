package lt.markmerkk.file_audio_streamer.fs.entities

data class IndexStats(
    val isIndexedCategories: Boolean = false,
    val categoryCount: Int = 0,
    val isIndexedBooks: Boolean = false,
    val bookCount: Int = 0,
    val isIndexedTracks: Boolean = false,
    val trackCount: Int = 0,
    val isIndexedEmptyBooks: Boolean = false,
    val emptyBookCount: Int = 0,
) {

    fun reportInline(): String {
        return report().replace("\n", " / ")
    }

    fun report(): String {
        val sb = StringBuilder()
        sb.append(generateState("Categories", isIndexedCategories, categoryCount))
        sb.append("\n")
        sb.append(generateState("Books", isIndexedBooks, bookCount))
        sb.append("\n")
        sb.append(generateState("Tracks", isIndexedTracks, trackCount))
        sb.append("\n")
        sb.append(generateState("Empty books", isIndexedEmptyBooks, emptyBookCount))
        return sb.toString()
    }

    private fun generateState(statisticName: String, isIndexed: Boolean, count: Int): String {
        return if (isIndexed) {
            "${statisticName}: $count"
        } else {
            "${statisticName}: Indexing.."
        }
    }

    companion object {
        fun asEmpty() = IndexStats()
        fun withCats(categoryCount: Int): IndexStats {
            return IndexStats(
                isIndexedCategories = true,
                categoryCount = categoryCount
            )
        }

        fun withCatsBooks(categoryCount: Int, bookCount: Int): IndexStats {
            return IndexStats(
                isIndexedCategories = true,
                categoryCount = categoryCount,
                isIndexedBooks = true,
                bookCount = bookCount,
            )
        }

        fun withCatsBooksTracks(
            categoryCount: Int,
            bookCount: Int,
            trackCount: Int,
        ): IndexStats {
            return IndexStats(
                isIndexedCategories = true,
                categoryCount = categoryCount,
                isIndexedBooks = true,
                bookCount = bookCount,
                isIndexedTracks = true,
                trackCount = trackCount,
            )
        }

        fun withCatsBooksTracksEB(
            categoryCount: Int,
            bookCount: Int,
            trackCount: Int,
            emptyBookCount: Int,
        ): IndexStats {
            return IndexStats(
                isIndexedCategories = true,
                categoryCount = categoryCount,
                isIndexedBooks = true,
                bookCount = bookCount,
                isIndexedTracks = true,
                trackCount = trackCount,
                isIndexedEmptyBooks = true,
                emptyBookCount = emptyBookCount,
            )
        }
    }
}
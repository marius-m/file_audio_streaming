package lt.markmerkk.file_audio_streamer.fs.entities

data class IndexStats(
    val hasIndexedRootEntries: Boolean = false,
    val rootEntryCount: Int = 0,
    val hasIndexedCategories: Boolean = false,
    val categoryCount: Int = 0,
    val hasIndexedBooks: Boolean = false,
    val bookCount: Int = 0,
    val hasIndexedTracks: Boolean = false,
    val trackCount: Int = 0,
    val hasIndexedEmptyBooks: Boolean = false,
    val emptyBookCount: Int = 0,
) {

    fun reportInline(): String {
        return report().replace("\n", " / ")
    }

    fun report(): String {
        val sb = StringBuilder()
        sb.append(generateState("Root entries", hasIndexedRootEntries, rootEntryCount))
        sb.append("\n")
        sb.append(generateState("Categories", hasIndexedCategories, categoryCount))
        sb.append("\n")
        sb.append(generateState("Books", hasIndexedBooks, bookCount))
        sb.append("\n")
        sb.append(generateState("Tracks", hasIndexedTracks, trackCount))
        sb.append("\n")
        sb.append(generateState("Empty books", hasIndexedEmptyBooks, emptyBookCount))
        return sb.toString()
    }

    private fun generateState(statisticName: String, isIndexed: Boolean, count: Int): String {
        return if (isIndexed) {
            "${statisticName}: $count"
        } else {
            "${statisticName}: Indexing.."
        }
    }

    class IndexStatsBuilder(
        private var hasIndexedRootEntries: Boolean = false,
        private var rootEntryCount: Int = 0,
        private var hasIndexedCategories: Boolean = false,
        private var categoryCount: Int = 0,
        private var hasIndexedBooks: Boolean = false,
        private var bookCount: Int = 0,
        private var hasIndexedTracks: Boolean = false,
        private var trackCount: Int = 0,
        private var hasIndexedEmptyBooks: Boolean = false,
        private var emptyBookCount: Int = 0,
    ) {

        fun appendRootEntries(rootEntryCount: Int): IndexStatsBuilder {
            this.hasIndexedRootEntries = true
            this.rootEntryCount = rootEntryCount
            return this
        }

        fun appendCategories(categoryCount: Int): IndexStatsBuilder {
            this.hasIndexedCategories = true
            this.categoryCount = categoryCount
            return this
        }

        fun appendBooks(bookCount: Int): IndexStatsBuilder {
            this.hasIndexedBooks = true
            this.bookCount = bookCount
            return this
        }

        fun appendTracks(trackCount: Int): IndexStatsBuilder {
            this.hasIndexedTracks = true
            this.trackCount = trackCount
            return this
        }

        fun appendEmptyBooks(emptyBookCount: Int): IndexStatsBuilder {
            this.hasIndexedEmptyBooks = true
            this.emptyBookCount = emptyBookCount
            return this
        }

        fun build(): IndexStats {
            return IndexStats(
                hasIndexedRootEntries = this.hasIndexedRootEntries,
                rootEntryCount = this.rootEntryCount,
                hasIndexedCategories = this.hasIndexedCategories,
                categoryCount = this.categoryCount,
                hasIndexedBooks = this.hasIndexedBooks,
                bookCount = this.bookCount,
                hasIndexedTracks = this.hasIndexedTracks,
                trackCount = this.trackCount,
                hasIndexedEmptyBooks = this.hasIndexedEmptyBooks,
                emptyBookCount = this.emptyBookCount,
            )
        }
    }

    companion object {
        fun asEmpty() = IndexStats()
    }
}
package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.Book
import javax.persistence.*

@Entity(name = "book")
class BookEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        @Column val categoryId: String,
        @Column(unique = true) val localId: String,
        @Column val title: String,
        @Column val titleEng: String,
        @Column val path: String
) {

    fun toBook(): Book = Book(
            categoryId = categoryId,
            id = localId,
            title = title,
            path = path
    )

    companion object {
        fun from(book: Book): BookEntity {
            return BookEntity(
                    categoryId = book.categoryId,
                    localId = book.id,
                    title = book.title,
                    titleEng = book.titleEng,
                    path = book.path
            )
        }
    }
}
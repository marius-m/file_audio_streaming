package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.Category
import javax.persistence.*

@Entity(name = "category")
class CategoryEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        @Column(unique = true) val localId: String,
        @Column val title: String,
        @Column val path: String
) {

    fun toCategory(): Category = Category(
            id = localId,
            title = title,
            path = path
    )

    companion object {
        fun from(category: Category): CategoryEntity {
            return CategoryEntity(
                    localId = category.id,
                    title = category.title,
                    path = category.path
            )
        }
    }
}
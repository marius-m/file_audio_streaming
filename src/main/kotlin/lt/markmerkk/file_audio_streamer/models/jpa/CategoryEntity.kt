package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.Category
import javax.persistence.*

@Entity(name = "category")
class CategoryEntity(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        @Column val rootEntryId: String,
        @Column(unique = true) val localId: String,
        @Column val title: String,
        @Column val titleEng: String,
        @Column val path: String
) {

    fun toCategory(): Category = Category(
        rootEntryId = rootEntryId,
        id = localId,
        title = title,
        path = path
    )

    companion object {
        fun from(category: Category): CategoryEntity {
            return CategoryEntity(
                rootEntryId = category.rootEntryId,
                localId = category.id,
                title = category.title,
                titleEng = category.titleEng,
                path = category.path
            )
        }
    }
}
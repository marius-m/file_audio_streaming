package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.models.CategoryFile
import java.time.OffsetDateTime
import javax.persistence.*

@Entity(name = "category")
class CategoryEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
    @Column val rootEntryId: String,
    @Column(unique = true) val localId: String,
    @Column val title: String,
    @Column val titleEng: String,
    @Column val path: String,
    @Column val createdAt: OffsetDateTime,
    @Column val updatedAt: OffsetDateTime,
) {

    fun toCategory(): CategoryFile = CategoryFile(
        rootEntryId = rootEntryId,
        _id = localId,
        _title = title,
        path = path,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun from(category: CategoryFile): CategoryEntity {
            return CategoryEntity(
                rootEntryId = category.rootEntryId,
                localId = category.id,
                title = category.title,
                titleEng = category.titleEng,
                path = category.path,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt,
            )
        }
    }
}
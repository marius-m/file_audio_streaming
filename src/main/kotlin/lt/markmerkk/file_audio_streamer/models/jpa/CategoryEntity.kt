package lt.markmerkk.file_audio_streamer.models.jpa

import lt.markmerkk.file_audio_streamer.DateTimeUtils
import lt.markmerkk.file_audio_streamer.models.Category
import javax.persistence.*

@Entity(name = "category")
class CategoryEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
    @Column val rootEntryId: String,
    @Column(unique = true) val localId: String,
    @Column val title: String,
    @Column val titleEng: String,
    @Column val path: String,
    @Column val createdAt: String,
    @Column val updatedAt: String,
) {

    fun toCategory(): Category = Category(
        rootEntryId = rootEntryId,
        id = localId,
        title = title,
        path = path,
        createdAt = DateTimeUtils.parseFromString(createdAt),
        updatedAt = DateTimeUtils.parseFromString(updatedAt),
    )

    companion object {
        fun from(category: Category): CategoryEntity {
            return CategoryEntity(
                rootEntryId = category.rootEntryId,
                localId = category.id,
                title = category.title,
                titleEng = category.titleEng,
                path = category.path,
                createdAt = DateTimeUtils.formatToString(category.createdAt),
                updatedAt = DateTimeUtils.formatToString(category.updatedAt),
            )
        }
    }
}
package lt.markmerkk.file_audio_streamer.daos

import lt.markmerkk.file_audio_streamer.models.jpa.RootEntryEntity
import org.springframework.data.repository.CrudRepository

interface RootEntryDao : CrudRepository<RootEntryEntity, Long>

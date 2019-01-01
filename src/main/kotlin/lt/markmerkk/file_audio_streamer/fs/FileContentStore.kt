package lt.markmerkk.file_audio_streamer.fs

import org.springframework.content.commons.repository.Store
import org.springframework.content.rest.StoreRestResource


@StoreRestResource(path = "songs")
interface FileContentStore : Store<String>
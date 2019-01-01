package lt.markmerkk.file_audio_streamer.fs

import lt.markmerkk.file_audio_streamer.Consts
import org.springframework.content.commons.repository.Store
import org.springframework.content.rest.StoreRestResource


@StoreRestResource(path = Consts.ENDPOINT_TRACKS)
interface FileContentStore : Store<String>
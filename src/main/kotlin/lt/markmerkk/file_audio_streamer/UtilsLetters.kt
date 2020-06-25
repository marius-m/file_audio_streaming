package lt.markmerkk.file_audio_streamer

import com.ibm.icu.text.Transliterator


object UtilsLetters {

    private val BULGARIAN_TO_LATIN = "Bulgarian-Latin/BGN"
    private val bulgarianToLatin = Transliterator.getInstance(BULGARIAN_TO_LATIN)

    fun transliterateLowercase(input: String): String {
        return bulgarianToLatin.transliterate(input.toLowerCase())
    }
}


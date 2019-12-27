package lt.markmerkk.file_audio_streamer.configs

class Credentials(
        basicUsername: String?,
        basicPassword: String?
) {

    val username: String = basicUsername ?: ""
    val password: String = basicPassword ?: ""

    fun isEmpty(): Boolean = username.isEmpty() && password.isEmpty()

    override fun toString(): String {
        if (isEmpty()) {
            return "No credentials"
        }
        return "$username / $password"
    }

    companion object {
        fun asEmpty(): Credentials = Credentials("", "")
    }

}
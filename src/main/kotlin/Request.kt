import java.io.Serializable

data class Request(val command: String, val data: Any) : Serializable
package ir.roudi.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RequestModel(
    @SerialName("a")
    val action: String,
    @SerialName("p")
    val port: Int,
    @SerialName("pl")
    val payload: String? = null
) {

    fun toJson() : String {
        return Json.encodeToString(RequestModel.serializer(), this)
    }

}

object RequestAction {
    const val FORWARD = "f"
    const val CIRCUIT = "c"
}
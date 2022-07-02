package ir.roudi.crypto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class EncryptedContent(
    val key: String,
    val data: String
) {
    fun toJson() : String {
        return Json.encodeToString(EncryptedContent.serializer(), this)
    }
}

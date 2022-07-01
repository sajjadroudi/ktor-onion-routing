package ir.roudi.notification

import kotlinx.serialization.Serializable

@Serializable
data class TempNotif(
    val text: String,
    val user: String
)
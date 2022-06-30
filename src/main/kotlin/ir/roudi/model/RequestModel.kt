package ir.roudi.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestModel(
    val action: String,
    val port: Int,
    val payload: String? = null
)

object RequestAction {
    const val FORWARD = "forward"
    const val CIRCUIT = "circuit"
}
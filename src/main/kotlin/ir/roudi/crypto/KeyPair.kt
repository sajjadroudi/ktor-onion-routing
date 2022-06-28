package ir.roudi.crypto

import kotlinx.serialization.Serializable

@Serializable
data class KeyPair(
    val publicKey: String,
    val privateKey: String
)

package ir.roudi.directory

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger
import ir.roudi.crypto.CryptoHandler

@Serializable
data class Node private constructor(
    val id: Int,
    val name: String,
    val port: Int
) {

    @Transient
    private val keyPair = CryptoHandler.generateKeyPair()

    val publicKey: String = keyPair.publicKey
    val privateKey: String = keyPair.privateKey

    companion object {
        var lastId = AtomicInteger()

        fun create(name: String, port: Int) : Node {
            return Node(lastId.getAndIncrement(), name, port)
        }
    }
}

package ir.roudi.directory

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger
import ir.roudi.crypto.CryptoHandler
import kotlinx.serialization.Transient

@Serializable
data class Node private constructor(
    val id: Int,
    val name: String,
    val port: Int
) {

    companion object {
        var lastId = AtomicInteger()

        fun create(name: String, port: Int) : Node {
            return Node(lastId.getAndIncrement(), name, port)
        }
    }

}

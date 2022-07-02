package ir.roudi.directory

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.serialization.Transient

@Serializable
data class Node(
    val name: String,
    val port: Int,
    val publicKey: String
)
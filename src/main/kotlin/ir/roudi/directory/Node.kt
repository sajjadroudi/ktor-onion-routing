package ir.roudi.directory

import ir.roudi.notification.Notification
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

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

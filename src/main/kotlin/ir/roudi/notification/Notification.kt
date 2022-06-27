package ir.roudi.notification

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class Notification private constructor(
    val id: Int,
    val text: String,
    val userName: String
) {

    companion object {
        var lastId = AtomicInteger()

        fun create(text: String, userName: String) : Notification {
            return Notification(lastId.getAndIncrement(), text, userName)
        }
    }

}
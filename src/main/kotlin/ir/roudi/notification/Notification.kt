package ir.roudi.notification

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicInteger

@Serializable
data class Notification private constructor(
    val id: Int,
    val title: String
) {

    companion object {
        var lastId = AtomicInteger()

        fun create(title: String) : Notification {
            return Notification(lastId.getAndIncrement(), title)
        }
    }

}
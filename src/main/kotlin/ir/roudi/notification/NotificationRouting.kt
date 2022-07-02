package ir.roudi.notification

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ir.roudi.Logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Application.configureNotificationRouting() {

    routing {
        get("/") {
            Logger.log("NotificationRouting:get", "get all notifications")
            call.respond(HttpStatusCode.OK, NotificationDataSource.getAllNotifications())
        }

        post {
            Logger.log("NotificationRouting:post", "receive new notif to save")
            val notif = Json.decodeFromString<TempNotif>(call.receiveText())
            Notification.create(notif.text, notif.user).let {
                NotificationDataSource.saveNotification(it)
            }
            call.respondText("Notification saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

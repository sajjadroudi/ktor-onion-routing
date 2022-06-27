package ir.roudi.notification

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureNotificationRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, NotificationDataSource.getAllNotifications())
        }

        post {
            val params = call.receiveParameters()
            val text = params.getOrFail("text")
            val user = params.getOrFail("user")
            Notification.create(text, user).let {
                NotificationDataSource.saveNotification(it)
            }
            call.respondText("Notification saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

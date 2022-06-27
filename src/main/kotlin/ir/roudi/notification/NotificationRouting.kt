package ir.roudi.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.util.*
import ir.roudi.notification.DataSource
import ir.roudi.notification.Notification

fun Application.configureNotificationRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, DataSource.getAllNotifications())
        }

        post {
            val params = call.receiveParameters()
            val text = params.getOrFail("text")
            val user = params.getOrFail("user")
            Notification.create(text, user).let {
                DataSource.saveNotification(it)
            }
            call.respondText("Notification saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

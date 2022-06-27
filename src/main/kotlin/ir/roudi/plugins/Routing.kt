package ir.roudi.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.util.*
import ir.roudi.DataSource
import ir.roudi.Notification

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respond(DataSource.getAllNotifications(), status = HttpStatusCode.OK)
        }

        post {
            val title = call.receiveParameters().getOrFail("title")
            Notification.create(title).let {
                DataSource.saveNotification(it)
            }
            call.respond("Notification saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

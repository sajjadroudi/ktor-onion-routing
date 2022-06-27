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
            call.respond(HttpStatusCode.OK, DataSource.getAllNotifications())
        }

        post {
            val title = call.receiveParameters().getOrFail("title")
            Notification.create(title).let {
                DataSource.saveNotification(it)
            }
            call.respondText("Notification saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

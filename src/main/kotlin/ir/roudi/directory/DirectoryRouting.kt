package ir.roudi.directory

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureDirectoryRouting() {

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, DirectoryDataSource.getAllNodes())
        }

        post {
            val node = call.receive<Node>()
            DirectoryDataSource.saveNode(node)
            call.respondText("Node saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

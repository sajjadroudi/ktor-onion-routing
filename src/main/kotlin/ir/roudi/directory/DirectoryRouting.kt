package ir.roudi.directory

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ir.roudi.Logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureDirectoryRouting() {

    routing {
        get("/") {
            Logger.log("DirectoryRouting:get", "A get request has been received")
            val nodes = DirectoryDataSource.getAllNodes()
            val circuitId = DirectoryDataSource.getLastCircuitId()
            Logger.log("DirectoryRouting:get", "${nodes.size} node(s) is sent to the client")
            call.respond(HttpStatusCode.OK, NodesResponse(nodes, circuitId))
        }

        post {
            Logger.log("DirectoryRouting:post", "A node to save has been received.")
            DirectoryDataSource.saveNode(call.receive<Node>())
            call.respondText("Node saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

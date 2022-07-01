package ir.roudi.directory

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.configureDirectoryRouting() {

    routing {
        get("/") {
            val nodes = DirectoryDataSource.getAllNodes()
            val circuitId = DirectoryDataSource.getLastCircuitId()
            val response = Json.encodeToString(NodesResponse(nodes, circuitId))
            call.respond(HttpStatusCode.OK, response)
        }

        post {
            val node = call.receive<Node>()
            DirectoryDataSource.saveNode(node)
            call.respondText("Node saved successfully!", status = HttpStatusCode.Created)
        }
    }
}

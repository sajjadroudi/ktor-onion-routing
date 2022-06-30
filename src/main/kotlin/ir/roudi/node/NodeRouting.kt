package ir.roudi.node

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import ir.roudi.Config
import ir.roudi.crypto.CryptoHandler
import ir.roudi.model.RequestAction
import ir.roudi.model.RequestModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.internal.toNonNegativeInt

fun Application.configureNodeRouting(
    nodeServer: NodeServer
) {

    routing {

        post {
            val encryptedBody = call.receiveText()
            val bodyStr = nodeServer.decrypt(encryptedBody)
            val body = Json.decodeFromString<RequestModel>(bodyStr)
            val circuitId = call.request.header("circuit-id").toNonNegativeInt(0)
            if(body.action == RequestAction.CIRCUIT) {
                val originPort = body.port
                nodeServer.saveCircuitIdInfo(circuitId, originPort)
                call.respond(HttpStatusCode.OK)
            } else { // action == forward
                val destPort = body.port
                val encryptedData = body.payload
                val response : HttpResponse = ktorClient.post {
                    host = Config.LOCALHOST
                    port = destPort
                    setBody(encryptedData)
                }
                val responseBody = response.bodyAsChannel().toByteArray()
                val encryptedResponseBody = CryptoHandler.encryptWithPrivateKey(responseBody, nodeServer.privateKey)
                call.respond(status = response.status, message = encryptedResponseBody)
            }

        }

    }

}

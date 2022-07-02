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
import ir.roudi.crypto.EncryptedContent
import ir.roudi.crypto.KeyLoader
import ir.roudi.crypto.NewCryptoHandler
import ir.roudi.model.RequestAction
import ir.roudi.model.RequestModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.toNonNegativeInt

fun Application.configureNodeRouting(
    nodeServer: NodeServer
) {

    routing {

        post {
            val bodyText = call.receiveText()
            println("text: $bodyText")
            val encryptedBody = Json.decodeFromString<EncryptedContent>(bodyText)
//            val encryptedBody = call.receive<EncryptedContent>()
            val bodyStr = NewCryptoHandler.decrypt(encryptedBody, KeyLoader.loadPrivateKey(nodeServer.privateKey))
            val body = Json.decodeFromString<RequestModel>(bodyStr)
            val circuitId = call.request.header("circuit-id").toNonNegativeInt(0)
            if(body.action == RequestAction.CIRCUIT) {
                val originPort = body.port
                nodeServer.saveCircuitIdInfo(circuitId, originPort)
                call.respond(HttpStatusCode.OK, "Connection created!")
            } else if(body.payload.isNullOrEmpty()) { // action == forward
                val destPort = body.port
                println("kiridest: $destPort")
                val response : HttpResponse = ktorClient.get {
                    host = Config.LOCALHOST
                    port = destPort
                    headers["circuit-id"] = "$circuitId"
                }
                val responseBody = response.bodyAsText()
                val encryptedResponseBody = NewCryptoHandler.encrypt(responseBody, KeyLoader.loadPrivateKey(nodeServer.privateKey))
                println("mypb: ${Json.encodeToString(encryptedResponseBody)}")
                call.respond(status = response.status, message = encryptedResponseBody)
            } else {
                val destPort = body.port
                val encryptedData = body.payload
                println("mypa: $encryptedData")
                val response : HttpResponse = ktorClient.post {
                    host = Config.LOCALHOST
                    port = destPort
                    headers["circuit-id"] = "$circuitId"
                    setBody(encryptedData)
                }
                val responseBody = response.bodyAsText()
                val encryptedResponseBody = NewCryptoHandler.encrypt(responseBody, KeyLoader.loadPrivateKey(nodeServer.privateKey))
                println("mypb: ${Json.encodeToString(encryptedResponseBody)}")
                call.respond(status = response.status, message = encryptedResponseBody)
                call.respond(response.status)
            }

        }

    }

}

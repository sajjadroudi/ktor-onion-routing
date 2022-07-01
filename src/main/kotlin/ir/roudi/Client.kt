package ir.roudi

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ir.roudi.crypto.CryptoHandler
import ir.roudi.directory.Node
import ir.roudi.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Client(
    private val client: HttpClient
) {

    suspend fun createCircuit(nodes: List<Node>) {
        var request = RequestModel(RequestAction.CIRCUIT, Config.CLIENT_PORT)
        var requestBodyStr = Json.encodeToString(request)
        var encryptedBodyStr = CryptoHandler.encryptWithPublicKey(requestBodyStr, nodes[0].publicKey)
        var response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            setBody(encryptedBodyStr)
        }

        if(response.status.value !in 200..299)
            throw RuntimeException()

        var payload = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.CIRCUIT, nodes[0].port)), nodes[1].publicKey)
        encryptedBodyStr = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, nodes[1].port, payload)), nodes[0].publicKey)
        response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            setBody(encryptedBodyStr)
        }

        if(response.status.value !in 200..299)
            throw RuntimeException()

        payload = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.CIRCUIT, nodes[1].port)), nodes[2].publicKey)
        payload = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, nodes[2].port, payload)), nodes[1].publicKey)
        encryptedBodyStr = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, nodes[1].port, payload)), nodes[0].publicKey)
        response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            setBody(encryptedBodyStr)
        }

        if(response.status.value !in 200..299)
            throw RuntimeException()
    }

}
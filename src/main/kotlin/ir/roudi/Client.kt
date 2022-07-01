package ir.roudi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ir.roudi.crypto.CryptoHandler
import ir.roudi.directory.Node
import ir.roudi.directory.NodesResponse
import ir.roudi.model.*
import ir.roudi.notification.Notification
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ir.roudi.notification.TempNotif

class Client(
    private val client: HttpClient
) {

    private var nodes = listOf<Node>()
    private var circuitId = 0

    suspend fun initialize() {
        val nodes = getNodes()
        createCircuit(nodes.nodes)
        this.nodes = nodes.nodes
        this.circuitId = nodes.circuitId
    }

    private suspend fun getNodes() : NodesResponse {
        val response = client.get {
            host = Config.DIRECTORY_HOST
            port = Config.DIRECTORY_PORT
        }.body<NodesResponse>()

        val selectedNodes = response.nodes.toMutableList().shuffled().subList(0, 4)

        return NodesResponse(selectedNodes, response.circuitId)
    }

    private suspend fun createCircuit(nodes: List<Node>) {
        var request = RequestModel(RequestAction.CIRCUIT, Config.CLIENT_PORT)
        var requestBodyStr = Json.encodeToString(request)
        var encryptedBodyStr = CryptoHandler.encryptWithPublicKey(requestBodyStr, nodes[0].publicKey)
        var response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers.set("circuit-id", circuitId)
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

    suspend fun getNotifications() : List<Notification> {
        var response = client.get {
            host = Config.LOCALHOST
            port = nodes[0].port
        }.bodyAsText()

        response = CryptoHandler.decryptWithPublicKey(response, nodes[0].publicKey)
        response = CryptoHandler.decryptWithPublicKey(response, nodes[1].publicKey)
        response = CryptoHandler.decryptWithPublicKey(response, nodes[2].publicKey)

        return Json.decodeFromString<List<Notification>>(response)
    }

    suspend fun postNotification(text: String, user: String) {
        val notif = Json.encodeToString(TempNotif(text, user))

        var payload = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, Config.NOTIFICATION_PORT, notif)), nodes[2].publicKey)
        payload = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, nodes[2].port, payload)), nodes[1].publicKey)
        val encryptedBodyStr = CryptoHandler.encryptWithPublicKey(Json.encodeToString(RequestModel(RequestAction.FORWARD, nodes[1].port, payload)), nodes[0].publicKey)

        val response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers.set("circuit-id", circuitId)
            setBody(encryptedBodyStr)
        }

        if(response.status.value !in 200..299)
            throw RuntimeException()
    }

}
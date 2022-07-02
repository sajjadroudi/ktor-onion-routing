package ir.roudi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ir.roudi.crypto.EncryptedContent
import ir.roudi.crypto.KeyLoader
import ir.roudi.crypto.NewCryptoHandler
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
        this.circuitId = nodes.circuitId
        this.nodes = nodes.nodes
        createCircuit(nodes.nodes)
    }

    private suspend fun getNodes() : NodesResponse {
        val response = client.get {
            host = Config.DIRECTORY_HOST
            port = Config.DIRECTORY_PORT
        }.body<NodesResponse>()

        val selectedNodes = response.nodes.toMutableList().shuffled().subList(0, Math.min(response.nodes.size, 4))

        return NodesResponse(selectedNodes, response.circuitId)
    }

    private suspend fun createCircuit(nodes: List<Node>) {
        var encryptedBody = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.CIRCUIT, Config.CLIENT_PORT).toJson(),
            KeyLoader.loadPublicKey(nodes[0].publicKey)
        )
        var response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers["circuit-id"] = "$circuitId"
            setBody(encryptedBody.toJson())
        }

        if(response.status.value !in 200..299)
            throw RuntimeException()

        var payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.CIRCUIT, nodes[0].port).toJson(),
            KeyLoader.loadPublicKey(nodes[1].publicKey)
        ).toJson()
        encryptedBody = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[1].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[0].publicKey)
        )

        response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers.set("circuit-id", "$circuitId")
            setBody(encryptedBody.toJson())
        }

        if(response.status.value !in 200..299) {
//            throw RuntimeException()
        }

        payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.CIRCUIT, nodes[1].port).toJson(),
            KeyLoader.loadPublicKey(nodes[2].publicKey)
        ).toJson()
        payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[2].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[1].publicKey)
        ).toJson()
        encryptedBody = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[1].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[0].publicKey)
        )
        response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers.set("circuit-id", "$circuitId")
            setBody(encryptedBody.toJson())
        }

        if(response.status.value !in 200..299) {
//            throw RuntimeException()
        }
    }

    suspend fun getNotifications() : List<Notification> {
        var payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, Config.NOTIFICATION_PORT, "").toJson(),
            KeyLoader.loadPublicKey(nodes[2].publicKey)
        ).toJson()
        payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[2].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[1].publicKey)
        ).toJson()
        val encryptedBody = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[1].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[0].publicKey)
        )

        var response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers["circuit-id"] = "$circuitId"
            setBody(encryptedBody.toJson())
        }

        val textBody = response.bodyAsText()
        println("resres: ${textBody}")
        var res = Json.decodeFromString<EncryptedContent>(textBody)

        res = Json.decodeFromString<EncryptedContent>(NewCryptoHandler.decrypt(res, KeyLoader.loadPublicKey(nodes[0].publicKey)))
        res = Json.decodeFromString<EncryptedContent>(NewCryptoHandler.decrypt(res, KeyLoader.loadPublicKey(nodes[1].publicKey)))
        val responseStr = NewCryptoHandler.decrypt(res, KeyLoader.loadPublicKey(nodes[2].publicKey))

        return Json.decodeFromString<List<Notification>>(responseStr)
    }

    suspend fun postNotification(text: String, user: String) {
        val notif = Json.encodeToString(TempNotif(text, user))

        var payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, Config.NOTIFICATION_PORT, notif).toJson(),
            KeyLoader.loadPublicKey(nodes[2].publicKey)
        ).toJson()
        payload = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[2].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[1].publicKey)
        ).toJson()
        val encryptedBody = NewCryptoHandler.encrypt(
            RequestModel(RequestAction.FORWARD, nodes[1].port, payload).toJson(),
            KeyLoader.loadPublicKey(nodes[0].publicKey)
        )

        val response = client.post {
            host = Config.LOCALHOST
            port = nodes[0].port
            headers["circuit-id"] = "$circuitId"
            setBody(encryptedBody.toJson())
        }

        if(response.status.value !in 200..299) {
//            throw RuntimeException()
        }
    }

}
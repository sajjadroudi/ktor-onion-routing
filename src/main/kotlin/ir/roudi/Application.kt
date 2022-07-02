package ir.roudi

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.directory.Node
import ir.roudi.directory.configureDirectoryRouting
import ir.roudi.node.NodeServer
import ir.roudi.node.configureNodeRouting
import ir.roudi.node.ktorClient
import ir.roudi.notification.configureNotificationRouting
import ir.roudi.plugins.configureSerialization
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
    runDirectorySever()
    runNotificationServer()
    runNodeServer("node1", 8081)
    runNodeServer("node2", 8082)
    runNodeServer("node3", 8083)

    runBlocking {
        val client = Client(ktorClient)
        client.initialize()
        client.postNotification("t", "u")
        client.postNotification("t2", "u2")
//        println(client.getNotifications())
        System.err.println(client.getNotifications())
    }

    preventFinishing()
}

private fun preventFinishing() {
    while(true) {

    }
}

private fun runDirectorySever() {
    embeddedServer(Netty, host = Config.DIRECTORY_HOST, port = Config.DIRECTORY_PORT) {
        configureSerialization()
        configureDirectoryRouting()
    }.start(wait = false)
}

private fun runNotificationServer() {
    embeddedServer(Netty, host = Config.NOTIFICATION_HOST, port = Config.NOTIFICATION_PORT) {
        configureSerialization()
        configureNotificationRouting()
    }.start(wait = false)
}

private fun runNodeServer(name: String, port: Int) {
    val nodeServer = NodeServer(name, port)
    embeddedServer(Netty, host = Config.LOCALHOST, port = port) {
        configureSerialization()
        configureNodeRouting(nodeServer)
    }.start(wait = false)

    runBlocking {
        ktorClient.post {
            host = Config.DIRECTORY_HOST
            this.port = Config.DIRECTORY_PORT
            setBody(Node(name, port, nodeServer.publicKey))
            expectSuccess = true
        }
    }
}
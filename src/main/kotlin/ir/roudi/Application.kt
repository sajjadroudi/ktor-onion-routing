package ir.roudi

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.directory.Node
import ir.roudi.directory.configureDirectoryRouting
import ir.roudi.node.NodeServer
import ir.roudi.node.buildKtorClient
import ir.roudi.node.configureNodeRouting
import ir.roudi.notification.configureNotificationRouting
import ir.roudi.plugins.configureSerialization
import kotlinx.coroutines.runBlocking
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun main() {
    runNotificationServer()
    val nodesContent = readTestcaseFiles()
    Config.DIRECTORY_PORT = nodesContent.map { it.directoryPort }.first()
    runDirectorySever()

    nodesContent.forEach { runNodeServer(it.name, it.port) }

    runBlocking {
        val client = Client(buildKtorClient())
        val client2 = Client(buildKtorClient())

        client.initialize()
        client2.initialize()

        client2.postNotification("ct", "cu")
        client.postNotification("t", "u")
        client.postNotification("t2", "u2")
        System.err.println(client.getNotifications())
        client.postNotification("t3", "u3")
        System.err.println(client.getNotifications())
    }

    preventFinishing()
}

private fun readTestcaseFiles(): List<FileContent> {
    return Files.walk(Path.of(File("testcase").toURI()))
        .filter(Files::isRegularFile)
        .map {
            val scanner = Scanner(it.toFile())
            scanner.next()
            val name = scanner.next()
            scanner.next()
            val port = scanner.nextInt()
            scanner.next()
            val directoryPort = scanner.nextInt()
            scanner.close()

            FileContent(name, port, directoryPort)
        }
        .toList()
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
    val ktorClient = buildKtorClient()
    embeddedServer(Netty, host = Config.LOCALHOST, port = port) {
        configureSerialization()
        configureNodeRouting(ktorClient, nodeServer)
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
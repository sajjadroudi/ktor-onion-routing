package ir.roudi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.directory.configureDirectoryRouting
import ir.roudi.notification.configureNotificationRouting
import ir.roudi.plugins.*

fun main() {
    runDirectorySever(false)
    runNotificationServer()
}

private fun runDirectorySever(wait: Boolean = true) {
    embeddedServer(Netty, host = Config.DIRECTORY_HOST, port = Config.DIRECTORY_PORT) {
        configureDirectoryRouting()
        configureSerialization()
    }.start(wait = wait)
}

private fun runNotificationServer(wait: Boolean = true) {
    embeddedServer(Netty, host = Config.NOTIFICATION_HOST, port = Config.NOTIFICATION_PORT) {
        configureNotificationRouting()
        configureSerialization()
    }.start(wait = wait)
}
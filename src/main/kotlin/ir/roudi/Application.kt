package ir.roudi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.plugins.*

fun main() {
    runNotificationServer()
}

private fun runNotificationServer(wait: Boolean = true) {
    embeddedServer(Netty, host = Config.NOTIFICATION_HOST, port = Config.NOTIFICATION_PORT) {
        configureNotificationRouting()
        configureSerialization()
    }.start(wait = wait)
}
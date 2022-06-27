package ir.roudi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.plugins.*

fun main() {
    runNotificationServer()
}

private fun runNotificationServer(wait: Boolean = true) {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureNotificationRouting()
        configureSerialization()
    }.start(wait = wait)
}
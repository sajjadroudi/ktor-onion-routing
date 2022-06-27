package ir.roudi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ir.roudi.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}

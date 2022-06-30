package ir.roudi.node

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*

val ktorClient = HttpClient(OkHttp) {

    install(Logging) {
        level = LogLevel.ALL
    }

}
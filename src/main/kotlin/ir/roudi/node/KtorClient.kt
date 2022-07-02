package ir.roudi.node

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

val ktorClient = HttpClient(OkHttp) {

    install(Logging) {
        level = LogLevel.ALL
    }

    install(ContentNegotiation) {
        json()
    }

    install(DefaultRequest) {
//        header(HttpHeaders.ContentType, "application/json; charset=UTF-8")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

}
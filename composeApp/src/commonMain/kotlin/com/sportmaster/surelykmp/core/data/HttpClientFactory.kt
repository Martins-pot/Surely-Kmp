package com.sportmaster.surelykmp.core.data



import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor => $message")
                    }
                }
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                        explicitNulls = false
                    }
                )
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
                connectTimeoutMillis = 20_000L
            }

            // Add this to automatically throw exceptions on 4xx/5xx responses
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status
                    if (statusCode.value >= 400) {
                        throw ClientRequestException(response, "HTTP error: $statusCode")
                    }
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
package com.sportmaster.surelykmp.activities.freecodes.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {


    const val BASE_URL = "https://telegram-bot-2h7q.onrender.com/codes/"

    // Lazy initialization to avoid initialization issues on Android
    val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
}


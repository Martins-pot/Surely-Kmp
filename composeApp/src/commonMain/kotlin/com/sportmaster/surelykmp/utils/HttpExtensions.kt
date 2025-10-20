package com.sportmaster.surelykmp.utils


import io.ktor.client.request.*
import io.ktor.http.*

fun HttpRequestBuilder.bearerAuth(token: String) {
    headers {
        append(HttpHeaders.Authorization, "Bearer $token")
    }
}
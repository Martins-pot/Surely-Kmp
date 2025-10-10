package com.sportmaster.surelykmp.core.data.remote

import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import io.ktor.client.*
import io.ktor.client.request.*

class CodesApiService(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://srv442638.hstgr.cloud"
        private const val CODES_ENDPOINT = "$BASE_URL/codes/"
    }

    suspend fun getAllCodes(): Result<List<Code>, DataError.Remote> {
        return safeCall<List<Code>> {
            httpClient.get(CODES_ENDPOINT)
        }
    }
//
//    suspend fun loginUser(loginRequest: LoginRequest): TokenResponse {
//        return client.post("$BASE_URL/auth/login") {
//            setBody(loginRequest)
//        }
//    }
//
//    suspend fun registerUser(
//        username: String,
//        email: String,
//        password: String,
//        imageData: ByteArray
//    ): HttpResponse {
//        return client.post("$BASE_URL/auth/register") {
//            // Multipart form data
//        }
//    }
//
//    suspend fun fetchUserDetails(): List<User> {
//        return client.get("$BASE_URL/users")
//    }
//
//    suspend fun verifyOtp(email: String, otp: String): HttpResponse {
//        return client.post("$BASE_URL/auth/verify-otp") {
//            parameter("email", email)
//            parameter("otp", otp)
//        }
//    }

}

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
}
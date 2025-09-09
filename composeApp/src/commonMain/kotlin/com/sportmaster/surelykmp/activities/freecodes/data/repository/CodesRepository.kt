package com.sportmaster.surelykmp.activities.freecodes.data.repository


import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.data.network.ApiClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class CodesRepository {
    private val client = ApiClient.httpClient

    suspend fun getAllCodes(): List<Code> {
        return try {
            client.get("${ApiClient.BASE_URL}/codes/").body()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
package com.sportmaster.surelykmp.core.data.repository

import com.sportmaster.surelykmp.core.data.model.VersionCheckResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class VersionCheckRepository(private val httpClient: HttpClient) {

    suspend fun checkVersion(
        currentVersion: String,
        platform: String
    ): Result<VersionCheckResponse> {
        return try {
            val response = httpClient.get(
                "https://srv442638.hstgr.cloud/version/check"
            ) {
                parameter("version_str", currentVersion)
                parameter("platform", platform)
            }.body<VersionCheckResponse>()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
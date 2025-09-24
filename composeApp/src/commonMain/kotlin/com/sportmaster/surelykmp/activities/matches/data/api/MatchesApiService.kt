package com.sportmaster.surelykmp.activities.matches.data.api


import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MatchesApiService(private val httpClient: HttpClient) {

    companion object {
        private const val BASE_URL = "https://telegram-bot-2h7q.onrender.com"
    }

    suspend fun getMatches(): List<MatchItem> {
        return httpClient.get("$BASE_URL/match/").body()
    }

    suspend fun getPrediction(matchId: String): PredictionResponse {
        return httpClient.post("$BASE_URL/match/predict/$matchId").body()
    }
}
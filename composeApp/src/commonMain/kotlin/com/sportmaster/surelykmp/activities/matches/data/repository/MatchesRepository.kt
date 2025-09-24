package com.sportmaster.surelykmp.activities.matches.data.repository

import com.sportmaster.surelykmp.activities.matches.data.api.MatchesApiService
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse

class MatchesRepository(private val apiService: MatchesApiService) {

    suspend fun getMatches(): List<MatchItem> {
        return apiService.getMatches()
    }

    suspend fun getPrediction(matchId: String): PredictionResponse {
        return apiService.getPrediction(matchId)
    }

    fun getMatchesBySport(matches: List<MatchItem>, sport: String): List<MatchItem> {
        return matches.filter { it.sport.equals(sport, ignoreCase = true) }
    }
}
package com.sportmaster.surelykmp.activities.matches.domain

import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse
import com.sportmaster.surelykmp.activities.matches.data.repository.MatchesRepository


class GetMatchesUseCase(private val repository: MatchesRepository) {

    suspend fun execute(): List<MatchItem> {
        return repository.getMatches()
    }

    suspend fun executeForSport(sport: String): List<MatchItem> {
        val allMatches = repository.getMatches()
        return repository.getMatchesBySport(allMatches, sport)
    }
}





class GetPredictionUseCase(private val repository: MatchesRepository) {

    suspend fun execute(matchId: String): PredictionResponse {
        return repository.getPrediction(matchId)
    }
}
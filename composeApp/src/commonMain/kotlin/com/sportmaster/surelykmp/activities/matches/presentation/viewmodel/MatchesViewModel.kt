package com.sportmaster.surelykmp.activities.matches.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.matches.data.api.MatchesApiService
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse
import com.sportmaster.surelykmp.activities.matches.data.models.SportMatch
import com.sportmaster.surelykmp.activities.matches.domain.GetMatchesUseCase
import com.sportmaster.surelykmp.activities.matches.domain.GetPredictionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}

class MatchesViewModel(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val getPredictionUseCase: GetPredictionUseCase
) : ViewModel() {

    private val _selectedSport = MutableStateFlow(SportMatch.FOOTBALL)
    val selectedSport: StateFlow<SportMatch> = _selectedSport.asStateFlow()

    private val _matchesState = MutableStateFlow<UiState<List<MatchItem>>>(UiState.Loading)
    val matchesState: StateFlow<UiState<List<MatchItem>>> = _matchesState.asStateFlow()

    private val _predictionState = MutableStateFlow<UiState<PredictionResponse>>(UiState.Loading)
    val predictionState: StateFlow<UiState<PredictionResponse>> = _predictionState.asStateFlow()

    init {
        loadMatches()
    }

    fun selectSport(sport: SportMatch) {
        _selectedSport.value = sport
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _matchesState.value = UiState.Loading
            try {
                val matches = getMatchesUseCase.executeForSport(_selectedSport.value.apiName)
                _matchesState.value = UiState.Success(matches)
            } catch (e: Exception) {
                _matchesState.value = UiState.Error(e)
            }
        }
    }

    fun loadPrediction(matchId: String) {
        viewModelScope.launch {
            _predictionState.value = UiState.Loading
            try {
                val prediction = getPredictionUseCase.execute(matchId)
                _predictionState.value = UiState.Success(prediction)
            } catch (e: Exception) {
                _predictionState.value = UiState.Error(e)
            }
        }
    }
}
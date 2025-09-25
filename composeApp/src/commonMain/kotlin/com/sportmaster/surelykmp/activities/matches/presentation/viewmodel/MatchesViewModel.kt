package com.sportmaster.surelykmp.activities.matches.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PreferencesManager
import com.sportmaster.surelykmp.activities.matches.data.api.MatchesApiService
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse
import com.sportmaster.surelykmp.activities.matches.data.models.SportMatch
import com.sportmaster.surelykmp.activities.matches.domain.GetMatchesUseCase
import com.sportmaster.surelykmp.activities.matches.domain.GetPredictionUseCase
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//sealed class UiState<out T> {
//    object Loading : UiState<Nothing>()
//    data class Success<T>(val data: T) : UiState<T>()
//    data class Error(val exception: Throwable) : UiState<Nothing>()
//}
//
//class MatchesViewModel(
//    private val getMatchesUseCase: GetMatchesUseCase,
//    private val getPredictionUseCase: GetPredictionUseCase
//) : ViewModel() {
//
//    private val _selectedSport = MutableStateFlow(SportMatch.FOOTBALL)
//    val selectedSport: StateFlow<SportMatch> = _selectedSport.asStateFlow()
//
//    private val _matchesState = MutableStateFlow<UiState<List<MatchItem>>>(UiState.Loading)
//    val matchesState: StateFlow<UiState<List<MatchItem>>> = _matchesState.asStateFlow()
//
//    private val _predictionState = MutableStateFlow<UiState<PredictionResponse>>(UiState.Loading)
//    val predictionState: StateFlow<UiState<PredictionResponse>> = _predictionState.asStateFlow()
//
//    init {
//        loadMatches()
//    }
//
//    fun selectSport(sport: SportMatch) {
//        _selectedSport.value = sport
//        loadMatches()
//    }
//
//    fun loadMatches() {
//        viewModelScope.launch {
//            _matchesState.value = UiState.Loading
//            try {
//                val matches = getMatchesUseCase.executeForSport(_selectedSport.value.apiName)
//                _matchesState.value = UiState.Success(matches)
//            } catch (e: Exception) {
//                _matchesState.value = UiState.Error(e)
//            }
//        }
//    }
//
//    fun loadPrediction(matchId: String) {
//        viewModelScope.launch {
//            _predictionState.value = UiState.Loading
//            try {
//                val prediction = getPredictionUseCase.execute(matchId)
//                _predictionState.value = UiState.Success(prediction)
//            } catch (e: Exception) {
//                _predictionState.value = UiState.Error(e)
//            }
//        }
//    }
//}


sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}

data class PredictionPremiumState(
    val isSubscribed: Boolean = false,
    val isBlurActive: Boolean = true,
    val isTimerActive: Boolean = false,
    val timeRemaining: Long = 0L
)

// File: src/commonMain/kotlin/com/sportmaster/surelykmp/matches/presentation/viewmodel/MatchesViewModel.kt

class MatchesViewModel(
    private val getMatchesUseCase: GetMatchesUseCase,
    private val getPredictionUseCase: GetPredictionUseCase,
    private val unityAdsManager: UnityAdsManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _selectedSport = MutableStateFlow(SportMatch.FOOTBALL)
    val selectedSport: StateFlow<SportMatch> = _selectedSport.asStateFlow()

    private val _matchesState = MutableStateFlow<UiState<List<MatchItem>>>(UiState.Loading)
    val matchesState: StateFlow<UiState<List<MatchItem>>> = _matchesState.asStateFlow()

    private val _predictionState = MutableStateFlow<UiState<PredictionResponse>>(UiState.Loading)
    val predictionState: StateFlow<UiState<PredictionResponse>> = _predictionState.asStateFlow()

    // Premium/Blur state management for predictions
    private val _predictionPremiumState = MutableStateFlow(PredictionPremiumState())
    val predictionPremiumState: StateFlow<PredictionPremiumState> = _predictionPremiumState.asStateFlow()

    private var timerJob: Job? = null

    // Ad configuration
    private val adPlacementId = "Rewarded_Android"
    private val gameId = "5952135"

    // Preferences keys for predictions timer
    private val PREDICTION_START_TIME_KEY = "prediction_premium_start_time"
    private val PREDICTION_END_TIME_KEY = "prediction_premium_end_time"

    init {
        initializeAds()
        checkSavedPredictionTimerState()
        checkSubscriptionStatus()
        loadMatches()
    }

    private fun initializeAds() {
        viewModelScope.launch {
            try {
                unityAdsManager.initializeAds(gameId)
                unityAdsManager.loadRewardedAd(adPlacementId)
            } catch (e: Exception) {
                println("Failed to initialize ads: ${e.message}")
            }
        }
    }

    fun selectSport(sport: SportMatch) {
        _selectedSport.value = sport
        loadMatches()
    }
    private val _allMatches = MutableStateFlow<List<MatchItem>>(emptyList())

    fun loadMatches() {
        viewModelScope.launch {
            _matchesState.value = UiState.Loading
            try {
                // Load ALL matches first
                val allMatches = getMatchesUseCase.execute() // Get all matches
                _allMatches.value = allMatches

                // Filter for display
                val filteredMatches = allMatches.filter {
                    it.sport.equals(_selectedSport.value.apiName, ignoreCase = true)
                }
                _matchesState.value = UiState.Success(allMatches) // Store all matches in state for lookup
            } catch (e: Exception) {
                _matchesState.value = UiState.Error(e)
            }
        }
    }

    // Add method to get filtered matches for display
    fun getFilteredMatches(): List<MatchItem> {
        return when (val currentState = _matchesState.value) {
            is UiState.Success -> currentState.data.filter {
                it.sport.equals(_selectedSport.value.apiName, ignoreCase = true)
            }
            else -> emptyList()
        }
    }

//    fun loadMatches() {
//        viewModelScope.launch {
//            _matchesState.value = UiState.Loading
//            try {
//                val matches = getMatchesUseCase.executeForSport(_selectedSport.value.apiName)
//                _matchesState.value = UiState.Success(matches)
//            } catch (e: Exception) {
//                _matchesState.value = UiState.Error(e)
//            }
//        }
//    }

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

    // Ad functionality for predictions
    fun onWatchPredictionAdClicked() {
        viewModelScope.launch {
            if (unityAdsManager.isAdReady(adPlacementId)) {
                unityAdsManager.showRewardedAd(
                    placementId = adPlacementId,
                    onAdCompleted = {
                        onWatchPredictionAdCompleted()
                        // Load next ad
                        unityAdsManager.loadRewardedAd(adPlacementId)
                    },
                    onAdFailed = { error ->
                        println("Ad failed: $error")
                        // Try to load a new ad
                        unityAdsManager.loadRewardedAd(adPlacementId)
                    }
                )
            } else {
                // Load ad and try again
                unityAdsManager.loadRewardedAd(adPlacementId)
            }
        }
    }

    fun onWatchPredictionAdCompleted() {
        startPredictionTimer()
    }

    fun onPredictionSubscriptionPurchased() {
        _predictionPremiumState.value = _predictionPremiumState.value.copy(
            isSubscribed = true,
            isBlurActive = false,
            isTimerActive = false,
            timeRemaining = 0L
        )
        cancelPredictionTimer()
    }

    private fun startPredictionTimer() {
        val startTime = getTimeMillis()
        val endTime = startTime + (20 * 60 * 1000L) // 20 minutes

        savePredictionTimerToPreferences(startTime, endTime)

        _predictionPremiumState.value = _predictionPremiumState.value.copy(
            isBlurActive = false,
            isTimerActive = true,
            timeRemaining = 20 * 60 * 1000L
        )

        startPredictionCountdownTimer(endTime)
    }

    private fun startPredictionCountdownTimer(endTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_predictionPremiumState.value.timeRemaining > 0) {
                delay(1000L)
                val currentTime = getTimeMillis()
                val remaining = endTime - currentTime

                if (remaining <= 0) {
                    _predictionPremiumState.value = _predictionPremiumState.value.copy(
                        isBlurActive = true,
                        isTimerActive = false,
                        timeRemaining = 0L
                    )
                    clearPredictionTimerFromPreferences()
                    break
                }

                _predictionPremiumState.value = _predictionPremiumState.value.copy(
                    timeRemaining = remaining
                )
            }
        }
    }

    private fun checkSavedPredictionTimerState() {
        val startTime = preferencesManager.getLong(PREDICTION_START_TIME_KEY, 0L)
        val endTime = preferencesManager.getLong(PREDICTION_END_TIME_KEY, 0L)
        val currentTime = getTimeMillis()

        if (startTime != 0L && endTime != 0L && currentTime < endTime) {
            val remaining = endTime - currentTime
            _predictionPremiumState.value = _predictionPremiumState.value.copy(
                isBlurActive = false,
                isTimerActive = true,
                timeRemaining = remaining
            )
            startPredictionCountdownTimer(endTime)
        }
    }

    private fun checkSubscriptionStatus() {
        viewModelScope.launch {
            val isSubscribed = checkUserSubscription()
            _predictionPremiumState.value = _predictionPremiumState.value.copy(
                isSubscribed = isSubscribed,
                isBlurActive = !isSubscribed
            )
        }
    }

    private suspend fun checkUserSubscription(): Boolean {
        // Implement your subscription check logic
        return false
    }

    private fun cancelPredictionTimer() {
        timerJob?.cancel()
        clearPredictionTimerFromPreferences()
    }

    private fun savePredictionTimerToPreferences(startTime: Long, endTime: Long) {
        preferencesManager.putLong(PREDICTION_START_TIME_KEY, startTime)
        preferencesManager.putLong(PREDICTION_END_TIME_KEY, endTime)
    }

    private fun clearPredictionTimerFromPreferences() {
        preferencesManager.remove(PREDICTION_START_TIME_KEY)
        preferencesManager.remove(PREDICTION_END_TIME_KEY)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
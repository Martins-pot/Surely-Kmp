package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant


// commonMain or shared module
data class PremiumState(
    val isSubscribed: Boolean = false,
    val isBlurActive: Boolean = true,
    val isTimerActive: Boolean = false,
    val timeRemaining: Long = 0L
)


class PremiumCodesViewModel(
    private val getCodesUseCase: GetCodesUseCase,
    private val unityAdsManager: UnityAdsManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    var selectedSport by mutableStateOf(Sport.FOOTBALL)
        private set

    var codes by mutableStateOf<List<Code>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    // Premium/Blur state management
    private val _premiumState = MutableStateFlow(PremiumState())
    val premiumState: StateFlow<PremiumState> = _premiumState.asStateFlow()

    private var timerJob: Job? = null

    // Ad configuration
    private val adPlacementId = "Rewarded_Android"
    private val gameId = "5952135"

    // Preferences keys
    private val START_TIME_KEY = "premium_start_time"
    private val END_TIME_KEY = "premium_end_time"

    init {
        initializeAds()
        checkSavedTimerState()
        checkSubscriptionStatus()
        loadCodes()
    }

    private fun initializeAds() {
        viewModelScope.launch {
            try {
                unityAdsManager.initializeAds(gameId)
                unityAdsManager.loadRewardedAd(adPlacementId)
            } catch (e: Exception) {
                // Handle initialization error
                println("Failed to initialize ads: ${e.message}")
            }
        }
    }

    fun selectSport(sport: Sport) {
        selectedSport = sport
        loadCodes()
    }

    fun onWatchAdClicked() {
        viewModelScope.launch {
            if (unityAdsManager.isAdReady(adPlacementId)) {
                unityAdsManager.showRewardedAd(
                    placementId = adPlacementId,
                    onAdCompleted = {
                        onWatchAdCompleted()
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

    fun onWatchAdCompleted() {
        startTimer()
    }

    fun onSubscriptionPurchased() {
        _premiumState.value = _premiumState.value.copy(
            isSubscribed = true,
            isBlurActive = false,
            isTimerActive = false,
            timeRemaining = 0L
        )
        cancelTimer()
    }

    fun retry() {
        loadCodes()
    }

    private fun startTimer() {
        val startTime = getTimeMillis()
        val endTime = startTime + (20 * 60 * 1000L) // 20 minutes

        saveTimerToPreferences(startTime, endTime)

        _premiumState.value = _premiumState.value.copy(
            isBlurActive = false,
            isTimerActive = true,
            timeRemaining = 20 * 60 * 1000L
        )

        startCountdownTimer(endTime)
    }

    private fun startCountdownTimer(endTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_premiumState.value.timeRemaining > 0) {
                delay(1000L)
                val currentTime = getTimeMillis()
                val remaining = endTime - currentTime

                if (remaining <= 0) {
                    _premiumState.value = _premiumState.value.copy(
                        isBlurActive = true,
                        isTimerActive = false,
                        timeRemaining = 0L
                    )
                    clearTimerFromPreferences()
                    break
                }

                _premiumState.value = _premiumState.value.copy(
                    timeRemaining = remaining
                )
            }
        }
    }

    private fun checkSavedTimerState() {
        val startTime = preferencesManager.getLong(START_TIME_KEY, 0L)
        val endTime = preferencesManager.getLong(END_TIME_KEY, 0L)
        val currentTime = getTimeMillis()

        if (startTime != 0L && endTime != 0L && currentTime < endTime) {
            val remaining = endTime - currentTime
            _premiumState.value = _premiumState.value.copy(
                isBlurActive = false,
                isTimerActive = true,
                timeRemaining = remaining
            )
            startCountdownTimer(endTime)
        }
    }

    private fun checkSubscriptionStatus() {
        viewModelScope.launch {
            val isSubscribed = checkUserSubscription()
            _premiumState.value = _premiumState.value.copy(
                isSubscribed = isSubscribed,
                isBlurActive = !isSubscribed
            )
        }
    }

    private suspend fun checkUserSubscription(): Boolean {
        // Implement your subscription check logic
        return false
    }

    private fun loadCodes() {
        viewModelScope.launch {
            isLoading = true
            error = null

            when (val result = getCodesUseCase.execute(selectedSport)) {
                is Result.Success -> {
                    codes = result.data.filter { it.isExpensive  && it.odds  in 1.5 .. 2000.0
                            && it.sport!!.equals(selectedSport.toString(), ignoreCase = true)
                    }
                        .sortedByDescending { code ->
                            try {
                                code.createdAt?.toInstant() ?: Instant.DISTANT_PAST
                            } catch (e: Exception) {
                                Instant.DISTANT_PAST
                            }
                        }
                    error = null
                }
                is Result.Error -> {
                    codes = emptyList()
                    error = when (result.error) {
                        DataError.Remote.NO_INTERNET -> "No internet connection"
                        DataError.Remote.REQUEST_TIMEOUT -> "Request timeout"
                        DataError.Remote.SERVER -> "Server error"
                        DataError.Remote.SERIALIZATION -> "Data parsing error"
                        DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests"
                        DataError.Remote.UNKNOWN -> "Unknown error occurred"
                    }
                }
            }

            isLoading = false
        }
    }

    private fun cancelTimer() {
        timerJob?.cancel()
        clearTimerFromPreferences()
    }

    private fun saveTimerToPreferences(startTime: Long, endTime: Long) {
        preferencesManager.putLong(START_TIME_KEY, startTime)
        preferencesManager.putLong(END_TIME_KEY, endTime)
    }

    private fun clearTimerFromPreferences() {
        preferencesManager.remove(START_TIME_KEY)
        preferencesManager.remove(END_TIME_KEY)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

// 2. Create the PreferencesManager interface and implementations
// commonMain
expect class PreferencesManager {
    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long): Long
    fun remove(key: String)
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String?): String?
}
package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ProfileState
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ProfileViewModel
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.koin.compose.koinInject

data class PremiumState(
    val isSubscribed: Boolean = false,
    val isBlurActive: Boolean = true,
    val isTimerActive: Boolean = false,
    val timeRemaining: Long = 0L
)

class PremiumCodesViewModel(
    private val getCodesUseCase: GetCodesUseCase,
    private val unityAdsManager: UnityAdsManager,
    private val preferencesManager: PreferencesManager,
    private val repository: ProfileRepository

    ) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()


    var selectedSport by mutableStateOf(Sport.FOOTBALL)
        private set

    var selectedCountry by mutableStateOf("default")
        private set

    var selectedFilter by mutableStateOf("default")
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
    private val COUNTRY_KEY = "selected_country"
    private val FILTER_KEY = "selected_filter"

    init {
        initializeAds()
        loadSavedPreferences()
        checkSavedTimerState()
        checkSubscriptionStatus()
        loadCodes()
    }

    private fun loadSavedPreferences() {
        selectedCountry = preferencesManager.getString(COUNTRY_KEY, "default") ?: "default"
        selectedFilter = preferencesManager.getString(FILTER_KEY, "default") ?: "default"
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

    fun selectSport(sport: Sport) {
        selectedSport = sport
        loadCodes()
    }

    fun selectCountry(country: String) {
        selectedCountry = country
        preferencesManager.putString(COUNTRY_KEY, country)
        loadCodes()
    }

    fun selectFilter(filter: String) {
        selectedFilter = filter
        preferencesManager.putString(FILTER_KEY, filter)
        loadCodes()
    }

    fun onWatchAdClicked() {
        viewModelScope.launch {
            if (unityAdsManager.isAdReady(adPlacementId)) {
                unityAdsManager.showRewardedAd(
                    placementId = adPlacementId,
                    onAdCompleted = {
                        onWatchAdCompleted()
                        unityAdsManager.loadRewardedAd(adPlacementId)
                    },
                    onAdFailed = { error ->
                        println("Ad failed: $error")
                        unityAdsManager.loadRewardedAd(adPlacementId)
                    }
                )
            } else {
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
        val isLoggedIn = repository.isUserLoggedIn()

        if (isLoggedIn) {
            _state.update { it.copy(isLoading = true, isLoggedIn = true) }

            val username = repository.getUsername()
            return username == "jugpo"
        }

        return false
    }

    private fun loadCodes() {
        viewModelScope.launch {
            isLoading = true
            error = null

            when (val result = getCodesUseCase.execute(selectedSport)) {
                is Result.Success -> {
                    // Filter by expensive codes
                    val expensiveCodes = result.data.filter {
                        it.isExpensive && it.odds in 1.2..2000.0
                    }

                    // Apply country filter
                    val countryFilteredCodes = if (selectedCountry == "default") {
                        expensiveCodes
                    } else {
                        expensiveCodes.filter {
                            it.country == selectedCountry || it.country == "unknown"
                        }
                    }

                    // Apply prediction filter
                    val finalFilteredCodes = when (selectedFilter.lowercase()) {
                        "high odds" -> countryFilteredCodes.filter {
                            it.odds != null && it.odds >= 30
                        }
                        "low risk" -> countryFilteredCodes.filter {
                            it.odds != null && it.odds < 10
                        }
                        "sure odds" -> countryFilteredCodes.filter {
                            it.accuracy != null && it.accuracy > 69
                        }
                        else -> countryFilteredCodes
                    }

                    // Sort by creation date
                    codes = finalFilteredCodes.sortedByDescending { code ->
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
                        DataError.Remote.USER_NOT_FOUND -> "User not found"
                        DataError.Remote.INVALID_CREDENTIALS -> "Incorrect password or username"
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

expect class PreferencesManager {
    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long): Long
    fun remove(key: String)
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String?): String?
}
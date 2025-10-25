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
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

class CodesViewModel(
    private val getCodesUseCase: GetCodesUseCase,
    private val unityAdsManager: UnityAdsManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

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

    // Ad configuration
    private val gameId = "5952135"

    // Preferences keys
    private val COUNTRY_KEY = "free_selected_country"
    private val FILTER_KEY = "free_selected_filter"

    init {
        initializeAds()
        loadSavedPreferences()
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
                // Banner ads load automatically after initialization
            } catch (e: Exception) {
                println("Failed to initialize banner ads: ${e.message}")
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

    fun retry() {
        loadCodes()
    }

    // NEW: Add refresh function for pull-to-refresh
    fun refreshCodes() {
        viewModelScope.launch {
            error = null

            when (val result = getCodesUseCase.execute(selectedSport)) {
                is Result.Success -> {
                    codes = applyFilters(result.data.filter { !it.isExpensive && it.odds in 1.2..2000.0 })
                    error = null
                }
                is Result.Error -> {
                    // Don't clear codes on refresh error, just show error
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
        }
    }

    private fun loadCodes() {
        viewModelScope.launch {
            isLoading = true
            error = null

            when (val result = getCodesUseCase.execute(selectedSport)) {
                is Result.Success -> {
                    codes = applyFilters(result.data.filter { !it.isExpensive && it.odds in 1.2..2000.0 })
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

    private fun applyFilters(codes: List<Code>): List<Code> {
        // Apply country filter
        val countryFilteredCodes = if (selectedCountry == "default") {
            codes
        } else {
            codes.filter {
                it.country == selectedCountry || it.country == "unknown"
            }
        }

        // Apply prediction filter
        return when (selectedFilter.lowercase()) {
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
        }.sortedByDescending { code ->
            try {
                code.createdAt?.toInstant() ?: Instant.DISTANT_PAST
            } catch (e: Exception) {
                Instant.DISTANT_PAST
            }
        }
    }
}


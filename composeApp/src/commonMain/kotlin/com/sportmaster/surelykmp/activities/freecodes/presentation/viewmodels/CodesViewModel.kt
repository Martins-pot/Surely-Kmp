package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result
import kotlinx.coroutines.launch

class CodesViewModel(
    private val getCodesUseCase: GetCodesUseCase
) : ViewModel() {

    var selectedSport by mutableStateOf(Sport.FOOTBALL)
        private set

    var codes by mutableStateOf<List<Code>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        loadCodes()
    }

    fun selectSport(sport: Sport) {
        selectedSport = sport
        loadCodes()
    }

    fun retry() {
        loadCodes()
    }

    private fun loadCodes() {
        viewModelScope.launch {
            isLoading = true
            error = null

            when (val result = getCodesUseCase.execute(selectedSport)) {
                is Result.Success -> {
                    codes = result.data
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
}
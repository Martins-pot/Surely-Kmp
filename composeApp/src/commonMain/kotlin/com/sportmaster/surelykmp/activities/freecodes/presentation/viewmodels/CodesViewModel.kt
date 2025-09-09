package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
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

    private fun loadCodes() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                codes = getCodesUseCase.execute(selectedSport)
            } catch (e: Exception) {
                error = "Failed to load codes: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
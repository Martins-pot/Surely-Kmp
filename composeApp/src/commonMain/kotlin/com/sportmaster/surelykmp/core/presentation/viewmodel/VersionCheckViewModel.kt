package com.sportmaster.surelykmp.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.core.domain.usecase.CheckAppVersionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VersionCheckState(
    val isLoading: Boolean = true,
    val needsUpdate: Boolean = false,
    val forceUpdate: Boolean = false,
    val latestVersion: String = "",
    val currentVersion: String = "",
    val error: String? = null
)

class VersionCheckViewModel(
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
    private val currentVersion: String,
    private val platform: String
) : ViewModel() {

    private val _state = MutableStateFlow(VersionCheckState())
    val state: StateFlow<VersionCheckState> = _state.asStateFlow()

    init {
        checkVersion()
    }

    fun checkVersion() {
        viewModelScope.launch {
            _state.value = VersionCheckState(
                isLoading = true,
                currentVersion = currentVersion
            )

            checkAppVersionUseCase(currentVersion, platform)
                .onSuccess { response ->
                    _state.value = VersionCheckState(
                        isLoading = false,
                        needsUpdate = response.needs_update,
                        forceUpdate = response.force_update,
                        latestVersion = response.latest_version,
                        currentVersion = response.current_version
                    )
                }
                .onFailure { error ->
                    // Don't block the app if version check fails
                    _state.value = VersionCheckState(
                        isLoading = false,
                        needsUpdate = false,
                        forceUpdate = false,
                        error = error.message
                    )
                }
        }
    }

    fun retry() {
        checkVersion()
    }
}
package com.sportmaster.surelykmp.activities.profile.presentation.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangePasswordState(
    val isLoading: Boolean = false,
    val username: String? = null,
    val avatarUrl: String? = null,
    val userId: String? = null,
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val newPasswordError: String? = null,
    val repeatPasswordError: String? = null,
    val isConfirmEnabled: Boolean = false,
    val showSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ChangePasswordAction {
    object LoadUserData : ChangePasswordAction
    data class NewPasswordChanged(val password: String) : ChangePasswordAction
    data class RepeatPasswordChanged(val password: String) : ChangePasswordAction
    object ToggleNewPasswordVisibility : ChangePasswordAction
    object ToggleRepeatPasswordVisibility : ChangePasswordAction
    object PickImage : ChangePasswordAction
    object ConfirmChanges : ChangePasswordAction
}

sealed interface ChangePasswordEvent {
    data class ShowError(val message: String) : ChangePasswordEvent
    data class ShowSuccess(val message: String) : ChangePasswordEvent
    object NavigateBack : ChangePasswordEvent
    object ShowImagePicker : ChangePasswordEvent
}

class ChangePasswordViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    private val eventChannel = Channel<ChangePasswordEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        loadUserData()
    }

    fun onAction(action: ChangePasswordAction) {
        when (action) {
            ChangePasswordAction.LoadUserData -> loadUserData()
            is ChangePasswordAction.NewPasswordChanged -> updateNewPassword(action.password)
            is ChangePasswordAction.RepeatPasswordChanged -> updateRepeatPassword(action.password)
            ChangePasswordAction.ToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            ChangePasswordAction.ToggleRepeatPasswordVisibility -> toggleRepeatPasswordVisibility()
            ChangePasswordAction.PickImage -> pickImage()
            ChangePasswordAction.ConfirmChanges -> confirmChanges()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val isLoggedIn = repository.isUserLoggedIn()
                if (isLoggedIn) {
                    val username = repository.getUsername()
                    val avatar = repository.getAvatar()
                    val userId = repository.getUserId()

                    _state.update {
                        it.copy(
                            username = username,
                            avatarUrl = avatar,
                            userId = userId,
                            isLoading = false
                        )
                    }
                } else {
                    // For non-logged-in users (forgot password flow)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            username = "Reset Password",
                            avatarUrl = null
                        )
                    }
                    // Don't navigate back - allow them to proceed with password reset
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load user data"
                    )
                }
            }
        }
    }

    private fun updateNewPassword(password: String) {
        _state.update { it.copy(newPassword = password) }
        validateNewPassword(password)
        updateConfirmButtonState()
    }

    private fun updateRepeatPassword(password: String) {
        _state.update { it.copy(repeatPassword = password) }
        validateRepeatPassword(password)
        updateConfirmButtonState()
    }

    private fun validateNewPassword(password: String) {
        val error = when {
            password.isEmpty() -> "Password cannot be empty"
            !isValidPassword(password) -> "Password must be at least 8 characters, include uppercase, lowercase, and a symbol"
            else -> null
        }

        _state.update { it.copy(newPasswordError = error) }
    }

    private fun validateRepeatPassword(password: String) {
        val error = when {
            password.isEmpty() -> "Please repeat your password"
            password != _state.value.newPassword -> "Passwords do not match"
            else -> null
        }

        _state.update { it.copy(repeatPasswordError = error) }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun updateConfirmButtonState() {
        val currentState = _state.value
        val isEnabled = currentState.newPassword.isNotEmpty() &&
                currentState.repeatPassword.isNotEmpty() &&
                currentState.newPasswordError == null &&
                currentState.repeatPasswordError == null &&
                currentState.newPassword == currentState.repeatPassword

        _state.update { it.copy(isConfirmEnabled = isEnabled) }
    }

    private fun toggleNewPasswordVisibility() {
        _state.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    private fun toggleRepeatPasswordVisibility() {
        _state.update { it.copy(isRepeatPasswordVisible = !it.isRepeatPasswordVisible) }
    }

    private fun pickImage() {
        viewModelScope.launch {
            eventChannel.send(ChangePasswordEvent.ShowImagePicker)
        }
    }

    private fun confirmChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val userId = _state.value.userId
                if (userId != null) {
                    val result = repository.updatePassword(userId, _state.value.newPassword)

                    when (result) {
                        is com.sportmaster.surelykmp.core.data.remote.Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    showSuccess = true
                                )
                            }
                            eventChannel.send(ChangePasswordEvent.ShowSuccess("Password changed successfully"))
                        }
                        is com.sportmaster.surelykmp.core.data.remote.Result.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to change password"
                                )
                            }
                            eventChannel.send(ChangePasswordEvent.ShowError("Failed to change password"))
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User ID not found"
                        )
                    }
                    eventChannel.send(ChangePasswordEvent.ShowError("User ID not found"))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to change password"
                    )
                }
                eventChannel.send(ChangePasswordEvent.ShowError("Failed to change password: ${e.message}"))
            }
        }
    }
}
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

data class AccountDetailsState(
    val isLoading: Boolean = false,
    val username: String? = null,
    val avatarUrl: String? = null,
    val userId: String? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val repeatPasswordError: String? = null,
    val showNewPasswordFields: Boolean = false,
    val isConfirmEnabled: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed interface AccountDetailsAction {
    object LoadUserData : AccountDetailsAction
    data class CurrentPasswordChanged(val password: String) : AccountDetailsAction
    data class NewPasswordChanged(val password: String) : AccountDetailsAction
    data class RepeatPasswordChanged(val password: String) : AccountDetailsAction
    object ToggleCurrentPasswordVisibility : AccountDetailsAction
    object ToggleNewPasswordVisibility : AccountDetailsAction
    object ToggleRepeatPasswordVisibility : AccountDetailsAction
    object PickImage : AccountDetailsAction
    object ConfirmChanges : AccountDetailsAction
    object ShowDeleteDialog : AccountDetailsAction
    object HideDeleteDialog : AccountDetailsAction
    object DeleteAccount : AccountDetailsAction
}

sealed interface AccountDetailsEvent {
    data class ShowError(val message: String) : AccountDetailsEvent
    data class ShowSuccess(val message: String) : AccountDetailsEvent
    object NavigateBack : AccountDetailsEvent
    object ShowImagePicker : AccountDetailsEvent
}

class AccountDetailsViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountDetailsState())
    val state: StateFlow<AccountDetailsState> = _state.asStateFlow()

    private val eventChannel = Channel<AccountDetailsEvent>()
    val events = eventChannel.receiveAsFlow()

    private var actualPassword: String? = null

    init {
        loadUserData()
    }

    fun onAction(action: AccountDetailsAction) {
        when (action) {
            AccountDetailsAction.LoadUserData -> loadUserData()
            is AccountDetailsAction.CurrentPasswordChanged -> updateCurrentPassword(action.password)
            is AccountDetailsAction.NewPasswordChanged -> updateNewPassword(action.password)
            is AccountDetailsAction.RepeatPasswordChanged -> updateRepeatPassword(action.password)
            AccountDetailsAction.ToggleCurrentPasswordVisibility -> toggleCurrentPasswordVisibility()
            AccountDetailsAction.ToggleNewPasswordVisibility -> toggleNewPasswordVisibility()
            AccountDetailsAction.ToggleRepeatPasswordVisibility -> toggleRepeatPasswordVisibility()
            AccountDetailsAction.PickImage -> pickImage()
            AccountDetailsAction.ConfirmChanges -> confirmChanges()
            AccountDetailsAction.ShowDeleteDialog -> showDeleteDialog()
            AccountDetailsAction.HideDeleteDialog -> hideDeleteDialog()
            AccountDetailsAction.DeleteAccount -> deleteAccount()
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
                    val storedPassword = repository.getStoredPassword()

                    _state.update {
                        it.copy(
                            username = username,
                            avatarUrl = avatar,
                            userId = userId,
                            isLoading = false
                        )
                    }

                    actualPassword = storedPassword

                    // Load additional user details if needed
                    username?.let { loadUserDetails(it) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                    eventChannel.send(AccountDetailsEvent.NavigateBack)
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

    private fun loadUserDetails(username: String) {
        viewModelScope.launch {
            try {
                when (val result = repository.fetchUserDetails(username)) {
                    is com.sportmaster.surelykmp.core.data.remote.Result.Success -> {
                        result.data?.let { user ->
                            // Update with latest user data
                            actualPassword = user.password
                        }
                    }
                    is com.sportmaster.surelykmp.core.data.remote.Result.Error -> {
                        // Handle error silently
                    }
                }
            } catch (e: Exception) {
                // Handle exception silently
            }
        }
    }

    private fun updateCurrentPassword(password: String) {
        _state.update { it.copy(currentPassword = password) }
        validateCurrentPassword(password)
        updateConfirmButtonState()
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

    private fun validateCurrentPassword(password: String) {
        val error = when {
            password.isEmpty() -> "Please enter your password"
            password != actualPassword -> "Incorrect password"
            else -> null
        }

        _state.update {
            it.copy(
                currentPasswordError = error,
                showNewPasswordFields = error == null && password.isNotEmpty() && password == actualPassword
            )
        }
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
        val isEnabled = currentState.showNewPasswordFields &&
                currentState.newPassword.isNotEmpty() &&
                currentState.repeatPassword.isNotEmpty() &&
                currentState.newPasswordError == null &&
                currentState.repeatPasswordError == null &&
                currentState.newPassword == currentState.repeatPassword

        _state.update { it.copy(isConfirmEnabled = isEnabled) }
    }

    private fun toggleCurrentPasswordVisibility() {
        _state.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
    }

    private fun toggleNewPasswordVisibility() {
        _state.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    private fun toggleRepeatPasswordVisibility() {
        _state.update { it.copy(isRepeatPasswordVisible = !it.isRepeatPasswordVisible) }
    }

    private fun pickImage() {
        viewModelScope.launch {
            eventChannel.send(AccountDetailsEvent.ShowImagePicker)
        }
    }

    private fun confirmChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val userId = _state.value.userId
                if (userId != null) {
                    val result = repository.updateUserProfile(
                        userId = userId,
                        newPassword = if (_state.value.newPassword.isNotEmpty()) _state.value.newPassword else null,
                        imageBytes = null // Implement image handling if needed
                    )

                    when (result) {
                        is com.sportmaster.surelykmp.core.data.remote.Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "Profile updated successfully"
                                )
                            }
                            eventChannel.send(AccountDetailsEvent.ShowSuccess("Profile updated successfully"))

                            // Clear passwords after successful update
                            _state.update {
                                it.copy(
                                    currentPassword = "",
                                    newPassword = "",
                                    repeatPassword = "",
                                    showNewPasswordFields = false
                                )
                            }
                        }
                        is com.sportmaster.surelykmp.core.data.remote.Result.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to update profile"
                                )
                            }
                            eventChannel.send(AccountDetailsEvent.ShowError("Failed to update profile"))
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User ID not found"
                        )
                    }
                    eventChannel.send(AccountDetailsEvent.ShowError("User ID not found"))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to update profile"
                    )
                }
                eventChannel.send(AccountDetailsEvent.ShowError("Failed to update profile"))
            }
        }
    }

    private fun showDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = true) }
    }

    private fun hideDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val userId = _state.value.userId
                if (userId != null) {
                    val result = repository.deleteUserAccount(userId)

                    when (result) {
                        is com.sportmaster.surelykmp.core.data.remote.Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    showDeleteDialog = false,
                                    successMessage = "Account deleted successfully"
                                )
                            }
                            eventChannel.send(AccountDetailsEvent.ShowSuccess("Account deleted successfully"))
                            eventChannel.send(AccountDetailsEvent.NavigateBack)
                        }
                        is com.sportmaster.surelykmp.core.data.remote.Result.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to delete account"
                                )
                            }
                            eventChannel.send(AccountDetailsEvent.ShowError("Failed to delete account"))
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User ID not found"
                        )
                    }
                    eventChannel.send(AccountDetailsEvent.ShowError("User ID not found"))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to delete account"
                    )
                }
                eventChannel.send(AccountDetailsEvent.ShowError("Failed to delete account"))
            }
        }
    }

    private fun updateProfileImage(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val userId = _state.value.userId
                if (userId != null) {
                    val result = repository.updateUserProfile(
                        userId = userId,
                        newPassword = null,
                        imageBytes = imageBytes
                    )

                    when (result) {
                        is com.sportmaster.surelykmp.core.data.remote.Result.Success -> {
                            // Reload user data to get updated avatar URL
                            loadUserData()
                            eventChannel.send(AccountDetailsEvent.ShowSuccess("Profile picture updated successfully"))
                        }
                        is com.sportmaster.surelykmp.core.data.remote.Result.Error -> {
                            eventChannel.send(AccountDetailsEvent.ShowError("Failed to update profile picture"))
                        }
                    }
                }
            } catch (e: Exception) {
                eventChannel.send(AccountDetailsEvent.ShowError("Failed to update profile picture"))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onImageSelected(imageUri: String?) {
        viewModelScope.launch {
            imageUri?.let { uri ->
                // Convert URI to ByteArray and update profile
                // Implement your image update logic here
                _state.update { it.copy(isLoading = true) }
                // Call repository to update profile image
                _state.update { it.copy(isLoading = false, avatarUrl = uri) }
            }
        }
    }

}
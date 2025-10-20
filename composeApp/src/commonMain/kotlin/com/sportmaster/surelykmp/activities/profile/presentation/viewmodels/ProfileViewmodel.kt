package com.sportmaster.surelykmp.activities.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.profile.data.User
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import com.sportmaster.surelykmp.core.data.remote.Result // Add this import
import com.sportmaster.surelykmp.utils.PlatformUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val userId: String? = null,
    val avatarUrl: String? = null,
    val isSubscribed: Boolean = false,
    val subscriptionStartTime: String? = null,
    val subscriptionEndTime: String? = null,
    val subscriptionStatus: String? = null,
    val versionName: String = "1.0.0",
    val errorMessage: String? = null
)

sealed interface ProfileAction {
    object OnLoginClick : ProfileAction
    object OnRegisterClick : ProfileAction
    object OnGetProClick : ProfileAction
    object OnAccountDetailsClick : ProfileAction
    object OnContactUsClick : ProfileAction
    object OnShareAppClick : ProfileAction
    object OnRateAppClick : ProfileAction
    object OnLogoutClick : ProfileAction
    object OnLoadUserProfile : ProfileAction
    data class OnShowTerms(val title: String, val content: String) : ProfileAction
}

sealed interface ProfileEvent {
    object NavigateToLogin : ProfileEvent
    object NavigateToRegister : ProfileEvent
    object NavigateToSubscription : ProfileEvent
    object NavigateToAccountDetails : ProfileEvent
    data class OpenEmail(val email: String) : ProfileEvent
    data class ShareApp(val appLink: String) : ProfileEvent
    object OpenRateApp : ProfileEvent
    data class ShowError(val message: String) : ProfileEvent
    data class ShowSuccess(val message: String) : ProfileEvent
}

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val platformUtils: PlatformUtils
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ProfileEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        loadUserProfile()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnLoginClick -> {
                viewModelScope.launch {
                    eventChannel.send(ProfileEvent.NavigateToLogin)
                }
            }
            ProfileAction.OnRegisterClick -> {
                viewModelScope.launch {
                    eventChannel.send(ProfileEvent.NavigateToRegister)
                }
            }
            ProfileAction.OnGetProClick -> {
                viewModelScope.launch {
                    if (_state.value.isLoggedIn) {
                        eventChannel.send(ProfileEvent.NavigateToSubscription)
                    } else {
                        eventChannel.send(ProfileEvent.ShowError("Log in to subscribe"))
                    }
                }
            }
            ProfileAction.OnAccountDetailsClick -> {
                viewModelScope.launch {
                    eventChannel.send(ProfileEvent.NavigateToAccountDetails)
                }
            }
            ProfileAction.OnContactUsClick -> {
                viewModelScope.launch {
                    eventChannel.send(ProfileEvent.OpenEmail("help@mertscript.com"))
                }
            }
            ProfileAction.OnShareAppClick -> {
                viewModelScope.launch {
                    val appLink = "https://play.google.com/store/apps/details?id=com.sportmaster.surelykmp"
                    eventChannel.send(ProfileEvent.ShareApp(appLink))
                }
            }
            ProfileAction.OnRateAppClick -> {
                viewModelScope.launch {
                    eventChannel.send(ProfileEvent.OpenRateApp)
                }
            }
            ProfileAction.OnLogoutClick -> {
                logout()
            }
            ProfileAction.OnLoadUserProfile -> {
                loadUserProfile()
            }
            is ProfileAction.OnShowTerms -> {
                // Handle showing terms - could send event if needed
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val isLoggedIn = repository.isUserLoggedIn()

            if (isLoggedIn) {
                _state.update { it.copy(isLoading = true, isLoggedIn = true) }

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

                // Use a more type-safe approach
                val result = repository.fetchUserDetails(username ?: "")
                if (result is Result.Success<*> && result.data is User) {
                    val userData = result.data
                    _state.update {
                        it.copy(
                            username = userData.userName,
                            email = userData.email,
                            avatarUrl = userData.avatar,
                            userId = userData.userId,
                            subscriptionStatus = userData.subscriptionStatus,
                            subscriptionStartTime = userData.subscriptionStartTime,
                            subscriptionEndTime = userData.subscriptionEndTime,
                            isSubscribed = checkSubscriptionStatus(
                                userData.subscriptionStatus,
                                userData.subscriptionEndTime
                            ),
                            isLoading = false
                        )
                    }
                    repository.saveUserData(userData)
                } else if (result is Result.Error<*>) {
                    _state.update { it.copy(isLoading = false) }
                }

                checkSubscription()
            } else {
                _state.update {
                    it.copy(
                        isLoggedIn = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun checkSubscription() {
        viewModelScope.launch {
            val status = repository.getSubscriptionStatus()
            val endTime = repository.getSubscriptionEndTime()
            val isSubscribed = checkSubscriptionStatus(status, endTime)
            _state.update { it.copy(isSubscribed = isSubscribed) }
        }
    }

    private fun checkSubscriptionStatus(status: String?, endTimeStr: String?): Boolean {
        if (status != "valid") return false

        return try {
            val endTime = endTimeStr?.toLongOrNull() ?: return false
            val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            endTime > currentTime
        } catch (e: Exception) {
            false
        }
    }

    private fun logout() {
        viewModelScope.launch {
            val rememberMe = repository.getRememberMe()

            if (!rememberMe) {
                repository.clearUserData()
                repository.clearTokens()
                repository.clearSubscriptionData()
            }

            _state.update {
                ProfileState(
                    isLoggedIn = false,
                    versionName = it.versionName
                )
            }

            eventChannel.send(ProfileEvent.ShowSuccess("Logged out successfully"))
        }
    }
}
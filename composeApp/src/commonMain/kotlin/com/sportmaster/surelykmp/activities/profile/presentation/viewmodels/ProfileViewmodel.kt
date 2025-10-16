package com.sportmaster.surelykmp.activities.profile.presentation.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoggedIn: Boolean = false,
    val isLoadingUser: Boolean = false,
    val username: String? = null,
    val userAvatar: String? = null,
    val userId: String? = null,
    val isSubscribed: Boolean = false,
    val versionText: String = "",
    val showTermsSheet: Boolean = false,
    val termsContent: String = "",
    val snackbarMessage: String? = null
)

class ProfileViewModel(
    private val userManager: UserManager,
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository,
    private val platformActions: PlatformActions
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUserState()
        loadVersionInfo()
    }

    private fun observeUserState() {
        viewModelScope.launch {
            combine(
                userManager.isLoggedIn,
                userManager.currentUser
            ) { isLoggedIn, user ->
                _uiState.update { it.copy(
                    isLoggedIn = isLoggedIn,
                    username = user?.userName,
                    userAvatar = user?.avatar,
                    userId = user?.userId
                )}

                if (isLoggedIn) {
                    checkSubscriptionStatus()
                    fetchUserDetails()
                }
            }.collect()
        }
    }

    private fun loadVersionInfo() {
        val version = platformActions.getAppVersion()
        _uiState.update { it.copy(versionText = "V.$version") }
    }

    private suspend fun fetchUserDetails() {
        _uiState.update { it.copy(isLoadingUser = true) }

        profileRepository.fetchUserDetails()
            .onSuccess { user ->
                userManager.updateUserInfo(user)
                _uiState.update {
                    it.copy(
                        isLoadingUser = false,
                        username = user.userName,
                        userAvatar = user.avatar,
                        userId = user.userId
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoadingUser = false) }

                // Check if token expired and retry
                if (tokenManager.isTokenExpiredException(error)) {
                    handleTokenExpiration()
                }
            }
    }

    private suspend fun handleTokenExpiration() {
        tokenManager.refreshAccessToken()
            .onSuccess {
                // Retry fetching user details
                fetchUserDetails()
            }
            .onFailure {
                showSnackbar("Session expired. Please login again.")
                logout()
            }
    }

    private suspend fun checkSubscriptionStatus() {
        profileRepository.getSubscriptionStatus()
            .onSuccess { isSubscribed ->
                _uiState.update { it.copy(isSubscribed = isSubscribed) }
            }
            .onFailure {
                _uiState.update { it.copy(isSubscribed = false) }
            }
    }

    fun logout() {
        viewModelScope.launch {
            userManager.logout()
            _uiState.update {
                ProfileUiState(
                    versionText = _uiState.value.versionText
                )
            }
            showSnackbar("Logged out successfully")
        }
    }

    fun toggleTermsView(show: Boolean) {
        if (show) {
            _uiState.update {
                it.copy(
                    showTermsSheet = true,
                    termsContent = getTermsOfService()
                )
            }
        } else {
            _uiState.update { it.copy(showTermsSheet = false) }
        }
    }

    fun togglePrivacyView(show: Boolean) {
        if (show) {
            _uiState.update {
                it.copy(
                    showTermsSheet = true,
                    termsContent = getPrivacyPolicy()
                )
            }
        } else {
            _uiState.update { it.copy(showTermsSheet = false) }
        }
    }

    fun onContactUsClick() {
        val success = platformActions.sendEmail(
            email = "help@mertscript.com",
            subject = "",
            message = ""
        )

        if (!success) {
            showSnackbar("Email client not found. Contact: help@mertscript.com")
            platformActions.copyToClipboard("help@mertscript.com")
        }
    }

    fun onShareAppClick() {
        platformActions.shareApp()
    }

    fun onRateAppClick() {
        platformActions.rateApp()
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(snackbarMessage = message) }
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    private fun getTermsOfService(): String {
        return """
            Terms of Service
            
            1. Introduction
            Welcome to ForeSport. By using our app, you agree to these Terms of Service.
            
            2. User Information
            We collect and store user information, including names, emails, and passwords. By using our app, you consent to this data collection.
            
            3. Predictions Disclaimer
            We provide predictions for SportyBet, 1xBet, Bet9ja, and BetKing. However, we are not responsible for any bets you place outside our app using these predictions.
            
            4. Subscriptions & Ads
            Our app includes subscriptions and advertisements to support our services.
            
            5. No Refunds
            All purchases and subscriptions are non-refundable. Please review your choices before making a payment.
            
            6. Non-Transferable Subscriptions
            Subscriptions are device-specific and cannot be transferred to another device. Even if an account is premium, it will only remain premium on the original device where the subscription was made.
            
            7. Notifications
            We may send notifications related to your account, updates, and promotions.
            
            8. Data Storage
            Your information is securely stored in our database.
            
            9. Changes to Terms
            We reserve the right to update these Terms at any time. Continued use of our app constitutes acceptance of the updated Terms.
            
            10. Contact Us
            If you have any questions about these Terms, please contact us.
        """.trimIndent()
    }

    private fun getPrivacyPolicy(): String {
        return """
            Privacy Policy
            
            1. Introduction
            Welcome to ForeSport. This Privacy Policy explains how we collect, use, and protect your personal information.
            
            2. Information We Collect
            We collect and store the following user data: names, emails, and passwords. By using our app, you consent to this data collection.
            
            3. Use of Information
            Your information is used for account management, security, and personalized experiences within the app.
            
            4. Predictions Disclaimer
            We provide predictions for SportyBet, 1xBet, Bet9ja, and BetKing. However, we are not responsible for any bets you place outside our app using these predictions.
            
            5. Subscriptions & Ads
            Our app includes subscriptions and advertisements to support our services.
            
            6. No Refunds
            All purchases and subscriptions are non-refundable. Please review your choices before making a payment.
            
            7. Non-Transferable Subscriptions
            Subscriptions are device-specific and cannot be transferred to another device.
            
            8. Notifications
            We may send notifications related to your account, updates, and promotions.
            
            9. Data Storage
            Your personal information is securely stored in our database and protected from unauthorized access.
            
            10. Changes to This Policy
            We reserve the right to update this Privacy Policy at any time. Continued use of our app constitutes acceptance of the updated policy.
            
            11. Contact Us
            If you have any questions regarding this Privacy Policy, please contact us.
        """.trimIndent()
    }
}

// Platform-specific actions interface
interface PlatformActions {
    fun getAppVersion(): String
    fun sendEmail(email: String, subject: String, message: String): Boolean
    fun shareApp()
    fun rateApp()
    fun copyToClipboard(text: String)
}
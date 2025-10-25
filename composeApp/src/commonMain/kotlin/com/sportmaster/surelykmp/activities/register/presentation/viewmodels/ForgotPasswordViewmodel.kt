package com.sportmaster.surelykmp.activities.register.presentation.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.domain.usecase.SendOtpUseCase
import com.sportmaster.surelykmp.core.domain.usecase.VerifyOtpUseCase
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import com.sportmaster.surelykmp.core.domain.usecase.CheckEmailExistsUseCase
import com.sportmaster.surelykmp.core.domain.usecase.SendOtpByEmailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val email: String = "",
    val emailError: String? = null,
    val isConfirmEnabled: Boolean = false,
    val showOtpScreen: Boolean = false,
    val resendCountdown: Int = 0,
    val resendCount: Int = 0
    // Removed: username field is no longer needed
)

sealed interface ForgotPasswordAction {
    data class EmailChanged(val email: String) : ForgotPasswordAction
    object SendOtp : ForgotPasswordAction
    data class VerifyOtp(val otp: String) : ForgotPasswordAction
    object ResendOtp : ForgotPasswordAction
}

sealed interface ForgotPasswordEvent {
    data class ShowError(val message: String) : ForgotPasswordEvent
    data class ShowSuccess(val message: String) : ForgotPasswordEvent
    object NavigateBack : ForgotPasswordEvent
    data class NavigateToChangePassword(val email: String) : ForgotPasswordEvent
}

class ForgotPasswordViewModel(
    private val repository: ProfileRepository,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase,
    private val sendOtpByEmailUseCase: SendOtpByEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val eventChannel = Channel<ForgotPasswordEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.EmailChanged -> updateEmail(action.email)
            ForgotPasswordAction.SendOtp -> sendOtp()
            is ForgotPasswordAction.VerifyOtp -> verifyOtp(action.otp)
            ForgotPasswordAction.ResendOtp -> resendOtp()
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email.trim()) }
        validateEmail(email.trim())
        updateConfirmButtonState()
    }

    private fun validateEmail(email: String) {
        val error = when {
            email.isEmpty() -> "Email cannot be empty"
            !isValidEmail(email) -> "Invalid email format"
            else -> null
        }

        _state.update { it.copy(emailError = error) }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun updateConfirmButtonState() {
        val currentState = _state.value
        val isEnabled = currentState.email.isNotEmpty() &&
                currentState.emailError == null

        _state.update { it.copy(isConfirmEnabled = isEnabled) }
    }

    private fun sendOtp() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Verifying info"
                )
            }

            try {
                // Check if email exists using the new endpoint
                when (val existsResult = checkEmailExistsUseCase(_state.value.email)) {
                    is Result.Success -> {
                        if (!existsResult.data) {
                            // Email doesn't exist
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    emailError = "Email not found"
                                )
                            }
                            eventChannel.send(ForgotPasswordEvent.ShowError("Email not found"))
                            return@launch
                        }

                        // Email exists, send OTP directly without needing username
                        when (val otpResult = sendOtpByEmailUseCase(_state.value.email)) {
                            is Result.Success<*> -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        showOtpScreen = true,
                                        resendCount = it.resendCount + 1
                                    )
                                }
                                startResendCountdown()
                                eventChannel.send(ForgotPasswordEvent.ShowSuccess("OTP sent to ${_state.value.email}"))
                            }
                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        emailError = "Failed to send OTP"
                                    )
                                }
                                eventChannel.send(ForgotPasswordEvent.ShowError("Failed to send OTP"))
                            }
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                emailError = "Error checking email"
                            )
                        }
                        eventChannel.send(ForgotPasswordEvent.ShowError("Error checking email"))
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        emailError = "An error occurred"
                    )
                }
                eventChannel.send(ForgotPasswordEvent.ShowError("An error occurred: ${e.message}"))
            }
        }
    }

    private fun resendOtp() {
        if (_state.value.resendCount >= 4) {
            viewModelScope.launch {
                eventChannel.send(ForgotPasswordEvent.ShowError("Maximum resend attempts reached. Try again later."))
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Requesting"
                )
            }

            try {
                // Use email-only OTP sending for resend as well
                when (val result = sendOtpByEmailUseCase(_state.value.email)) {
                    is Result.Success<*> -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                resendCount = it.resendCount + 1
                            )
                        }
                        startResendCountdown()
                        eventChannel.send(ForgotPasswordEvent.ShowSuccess("OTP resent successfully"))
                    }
                    is Result.Error<*> -> {
                        _state.update { it.copy(isLoading = false) }
                        eventChannel.send(ForgotPasswordEvent.ShowError("Failed to resend OTP"))
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                eventChannel.send(ForgotPasswordEvent.ShowError("An error occurred: ${e.message}"))
            }
        }
    }

    private fun verifyOtp(otp: String) {
        if (otp.length != 4) {
            viewModelScope.launch {
                eventChannel.send(ForgotPasswordEvent.ShowError("Please enter a valid 4-digit code"))
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Checking OTP"
                )
            }

            try {
                when (val result = verifyOtpUseCase(_state.value.email, otp)) {
                    is Result.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        eventChannel.send(ForgotPasswordEvent.ShowSuccess("Verification successful"))

                        // Navigate to change password screen
                        eventChannel.send(ForgotPasswordEvent.NavigateToChangePassword(_state.value.email))
                    }
                    is Result.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        eventChannel.send(ForgotPasswordEvent.ShowError("Verification failed. Please check your code."))
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                eventChannel.send(ForgotPasswordEvent.ShowError("An error occurred: ${e.message}"))
            }
        }
    }

//    private fun resendOtp() {
//        if (_state.value.resendCount >= 4) {
//            viewModelScope.launch {
//                eventChannel.send(ForgotPasswordEvent.ShowError("Maximum resend attempts reached. Try again later."))
//            }
//            return
//        }
//
//        viewModelScope.launch {
//            _state.update {
//                it.copy(
//                    isLoading = true,
//                    loadingMessage = "Requesting"
//                )
//            }
//
//            try {
//                val username = _state.value.username ?: ""
//                when (val result = sendOtpUseCase(_state.value.email, username)) {
//                    is Result.Success -> {
//                        _state.update {
//                            it.copy(
//                                isLoading = false,
//                                resendCount = it.resendCount + 1
//                            )
//                        }
//                        startResendCountdown()
//                        eventChannel.send(ForgotPasswordEvent.ShowSuccess("OTP resent successfully"))
//                    }
//                    is Result.Error -> {
//                        _state.update { it.copy(isLoading = false) }
//                        eventChannel.send(ForgotPasswordEvent.ShowError("Failed to resend OTP"))
//                    }
//                }
//            } catch (e: Exception) {
//                _state.update { it.copy(isLoading = false) }
//                eventChannel.send(ForgotPasswordEvent.ShowError("An error occurred: ${e.message}"))
//            }
//        }
//    }

    private fun startResendCountdown() {
        val countdownTime = if (_state.value.resendCount == 1) 30 else 50

        viewModelScope.launch {
            for (i in countdownTime downTo 0) {
                _state.update { it.copy(resendCountdown = i) }
                delay(1000)
            }
        }
    }
}
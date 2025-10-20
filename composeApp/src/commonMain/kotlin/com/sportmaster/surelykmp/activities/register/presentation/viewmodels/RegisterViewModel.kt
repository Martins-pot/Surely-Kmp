package com.sportmaster.surelykmp.activities.register.presentation.viewmodels
//
//

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.core.data.model.AuthState
import com.sportmaster.surelykmp.core.domain.usecase.*
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.data.remote.DataError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.io.IOException



class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val checkEmailAvailabilityUseCase: CheckEmailAvailabilityUseCase,
    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase,
    private val imageRepository: ImageRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _usernameError = MutableStateFlow<String?>(null)
    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _isLoginMode = MutableStateFlow(false)
    val isLoginMode: StateFlow<Boolean> = _isLoginMode.asStateFlow()

    private val _isEmailLoginMode = MutableStateFlow(true)
    val isEmailLoginMode: StateFlow<Boolean> = _isEmailLoginMode.asStateFlow()

    init {
//        println("RegisterViewModel initialized - UserPreferences: ${Clock.System.identityHashCode(userPreferences)}")
    }

    fun toggleMode() {
        _isLoginMode.value = !_isLoginMode.value
        clearErrors()
    }

    fun toggleLoginMethod(useEmail: Boolean) {
        _isEmailLoginMode.value = useEmail
    }

    fun checkEmailAvailability(email: String) {
        if (!isValidEmail(email)) {
            _emailError.value = "Invalid email format"
            return
        }

        viewModelScope.launch {
            when (val result = checkEmailAvailabilityUseCase(email)) {
                is Result.Success -> {
                    _emailError.value = if (result.data) "Email already exists" else null
                }
                is Result.Error -> {
                    _emailError.value = "Error checking email"
                }
            }
        }
    }

    fun checkUsernameAvailability(username: String) {
        if (username.isEmpty()) {
            _usernameError.value = "Username cannot be empty"
            return
        }

        if (username.length > 15) {
            _usernameError.value = "Username cannot exceed 15 characters"
            return
        }

        viewModelScope.launch {
            when (val result = checkUsernameAvailabilityUseCase(username)) {
                is Result.Success -> {
                    _usernameError.value = if (result.data) "Username already exists" else null
                }
                is Result.Error -> {
                    _usernameError.value = "Error checking username"
                }
            }
        }
    }

    fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                _passwordError.value = null
                true
            }
            password.length < 8 -> {
                _passwordError.value = "Password must be at least 8 characters"
                false
            }
            !password.any { it.isUpperCase() } -> {
                _passwordError.value = "Password must contain at least 1 uppercase letter"
                false
            }
            !password.any { it.isLowerCase() } -> {
                _passwordError.value = "Password must contain at least 1 lowercase letter"
                false
            }
            !password.any { !it.isLetterOrDigit() } -> {
                _passwordError.value = "Password must contain at least 1 special character"
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        imageUri: String
    ) {
        if (!validateRegistration(username, email, password, confirmPassword, imageUri)) {
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val imageBytes = imageRepository.readImageBytes(imageUri)
                if (imageBytes == null) {
                    _authState.value = AuthState.Error("Failed to read image file")
                    return@launch
                }

                when (val result = registerUserUseCase(username, email, password, imageBytes)) {
                    is Result.Success -> {
                        when (val otpResult = sendOtpUseCase(email, username)) {
                            is Result.Success -> {
                                _authState.value = AuthState.OtpSent
                            }
                            is Result.Error -> {
                                _authState.value = AuthState.Error(otpResult.error.toErrorMessage())
                            }
                        }
                    }
                    is Result.Error -> {
                        _authState.value = AuthState.Error(result.error.toErrorMessage())
                    }
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error("Failed to process image: ${e.message}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("An unexpected error occurred")
            }
        }
    }

    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit) {
        if (otp.length != 4) {
            _authState.value = AuthState.Error("Please enter a valid 4-digit code")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            when (val result = verifyOtpUseCase(email, otp)) {
                is Result.Success -> {
                    _authState.value = AuthState.Success("Verification successful")
                    onSuccess()
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(result.error.toErrorMessage())
                }
            }
        }
    }

    fun login(usernameOrEmail: String, password: String, isEmail: Boolean) {
        _authState.value = AuthState.Loading

        val username = if (isEmail) "" else usernameOrEmail
        val email = if (isEmail) usernameOrEmail else ""

        viewModelScope.launch {
            println("========================================")
            println("RegisterViewModel - Starting login")
            println("Username: $username, Email: $email")
            println("========================================")

            when (val result = loginUserUseCase(username, email, password)) {
                is Result.Success -> {
                    println("RegisterViewModel - Login SUCCESS")

                    // SAVE PASSWORD TO PREFERENCES
                    userPreferences.password = password

                    // Verify data was saved
                    println("RegisterViewModel - Checking saved data:")
                    println("Password saved: ${userPreferences.password != null}")

                    // Small delay to ensure persistence
                    kotlinx.coroutines.delay(200)

                    println("RegisterViewModel - After delay:")
                    println("Password: ${userPreferences.password}")

                    _authState.value = AuthState.Success("Login successful")
                }
                is Result.Error -> {
                    println("RegisterViewModel - Login FAILED: ${result.error}")
                    _authState.value = AuthState.Error(result.error.toErrorMessage())
                }
            }
        }
    }

    fun resendOtp(email: String, username: String) {
        viewModelScope.launch {
            when (val result = sendOtpUseCase(email, username)) {
                is Result.Success -> {
                    _authState.value = AuthState.Success("OTP resent successfully")
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(result.error.toErrorMessage())
                }
            }
        }
    }

    private fun validateRegistration(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        imageUri: String
    ): Boolean {
        when {
            username.isEmpty() -> {
                _authState.value = AuthState.Error("Username cannot be empty")
                return false
            }
            username.length > 15 -> {
                _authState.value = AuthState.Error("Username cannot exceed 15 characters")
                return false
            }
            !isValidEmail(email) -> {
                _authState.value = AuthState.Error("Invalid email format")
                return false
            }
            !validatePassword(password) -> {
                return false
            }
            password != confirmPassword -> {
                _authState.value = AuthState.Error("Passwords do not match")
                return false
            }
            imageUri.isEmpty() -> {
                _authState.value = AuthState.Error("Please select a profile picture")
                return false
            }
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun clearErrors() {
        _emailError.value = null
        _usernameError.value = null
        _passwordError.value = null
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun DataError.Remote.toErrorMessage(): String {
        return when (this) {
            DataError.Remote.REQUEST_TIMEOUT -> "Request timed out. Please try again."
            DataError.Remote.NO_INTERNET -> "No internet connection. Please check your network."
            DataError.Remote.SERVER -> "Server error. Please try again later."
            DataError.Remote.SERIALIZATION -> "Data error. Please try again."
            DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests. Please wait a moment."
            DataError.Remote.UNKNOWN -> "An unexpected error occurred. Please try again."
            DataError.Remote.USER_NOT_FOUND -> "User not found"
            DataError.Remote.INVALID_CREDENTIALS -> "Incorrect password or username"
        }
    }
}

interface ImageRepository {
    suspend fun readImageBytes(imagePath: String): ByteArray?
}


////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.viewModelScope
////import com.sportmaster.surelykmp.core.data.model.AuthState
////import com.sportmaster.surelykmp.core.domain.usecase.*
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.StateFlow
////import kotlinx.coroutines.flow.asStateFlow
////import kotlinx.coroutines.launch
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.sportmaster.surelykmp.core.data.model.AuthState
//import com.sportmaster.surelykmp.core.domain.usecase.*
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class RegisterViewModel(
//    private val registerUserUseCase: RegisterUserUseCase,
//    private val loginUserUseCase: LoginUserUseCase,
//    private val verifyOtpUseCase: VerifyOtpUseCase,
//    private val sendOtpUseCase: SendOtpUseCase,
//    private val checkEmailAvailabilityUseCase: CheckEmailAvailabilityUseCase,
//    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase
//) : ViewModel() {
//
//    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
//    val authState: StateFlow<AuthState> = _authState.asStateFlow()
//
//    private val _emailError = MutableStateFlow<String?>(null)
//    val emailError: StateFlow<String?> = _emailError.asStateFlow()
//
//    private val _usernameError = MutableStateFlow<String?>(null)
//    val usernameError: StateFlow<String?> = _usernameError.asStateFlow()
//
//    private val _passwordError = MutableStateFlow<String?>(null)
//    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()
//
//    private val _isLoginMode = MutableStateFlow(false)
//    val isLoginMode: StateFlow<Boolean> = _isLoginMode.asStateFlow()
//
//    private val _isEmailLoginMode = MutableStateFlow(true)
//    val isEmailLoginMode: StateFlow<Boolean> = _isEmailLoginMode.asStateFlow()
//
//    fun toggleMode() {
//        _isLoginMode.value = !_isLoginMode.value
//        clearErrors()
//    }
//
//    fun toggleLoginMethod(useEmail: Boolean) {
//        _isEmailLoginMode.value = useEmail
//    }
//
//    fun checkEmailAvailability(email: String) {
//        if (!isValidEmail(email)) {
//            _emailError.value = "Invalid email format"
//            return
//        }
//
//        viewModelScope.launch {
//            when (val result = checkEmailAvailabilityUseCase(email)) {
//                is Result.Success -> {
//                    _emailError.value = if (result.data) "Email already exists" else null
//                }
//                is Result.Error -> {
//                    _emailError.value = "Error checking email"
//                }
//            }
//        }
//    }
//
//    fun checkUsernameAvailability(username: String) {
//        if (username.isEmpty()) {
//            _usernameError.value = "Username cannot be empty"
//            return
//        }
//
//        if (username.length > 15) {
//            _usernameError.value = "Username cannot exceed 15 characters"
//            return
//        }
//
//        viewModelScope.launch {
//            when (val result = checkUsernameAvailabilityUseCase(username)) {
//                is Result.Success -> {
//                    _usernameError.value = if (result.data) "Username already exists" else null
//                }
//                is Result.Error -> {
//                    _usernameError.value = "Error checking username"
//                }
//            }
//        }
//    }
//
//    fun validatePassword(password: String): Boolean {
//        return when {
//            password.length < 8 -> {
//                _passwordError.value = "Password must be at least 8 characters"
//                false
//            }
//            !password.any { it.isUpperCase() } -> {
//                _passwordError.value = "Password must contain at least 1 uppercase letter"
//                false
//            }
//            !password.any { it.isLowerCase() } -> {
//                _passwordError.value = "Password must contain at least 1 lowercase letter"
//                false
//            }
//            !password.any { !it.isLetterOrDigit() } -> {
//                _passwordError.value = "Password must contain at least 1 special character"
//                false
//            }
//            else -> {
//                _passwordError.value = null
//                true
//            }
//        }
//    }
//
//    fun register(
//        username: String,
//        email: String,
//        password: String,
//        confirmPassword: String,
//        imageBytes: ByteArray?
//    ) {
//        if (!validateRegistration(username, email, password, confirmPassword, imageBytes)) {
//            return
//        }
//
//        _authState.value = AuthState.Loading
//
//        viewModelScope.launch {
//            when (val result = registerUserUseCase(username, email, password, imageBytes!!)) {
//                is Result.Success -> {
//                    when (val otpResult = sendOtpUseCase(email, username)) {
//                        is Result.Success -> {
//                            _authState.value = AuthState.OtpSent
//                        }
//                        is Result.Error -> {
//                            _authState.value = AuthState.Error(otpResult.error.toErrorMessage())
//                        }
//                    }
//                }
//                is Result.Error -> {
//                    _authState.value = AuthState.Error(result.error.toErrorMessage())
//                }
//            }
//        }
//    }
//
//    fun verifyOtp(email: String, otp: String, onSuccess: () -> Unit) {
//        if (otp.length != 4) {
//            _authState.value = AuthState.Error("Please enter a valid 4-digit code")
//            return
//        }
//
//        _authState.value = AuthState.Loading
//
//        viewModelScope.launch {
//            when (val result = verifyOtpUseCase(email, otp)) {
//                is Result.Success -> {
//                    _authState.value = AuthState.Success("Verification successful")
//                    onSuccess()
//                }
//                is Result.Error -> {
//                    _authState.value = AuthState.Error(result.error.toErrorMessage())
//                }
//            }
//        }
//    }
//
//    fun login(usernameOrEmail: String, password: String, isEmail: Boolean) {
//        _authState.value = AuthState.Loading
//
//        val username = if (isEmail) "" else usernameOrEmail
//        val email = if (isEmail) usernameOrEmail else ""
//
//        viewModelScope.launch {
//            when (val result = loginUserUseCase(username, email, password)) {
//                is Result.Success -> {
//                    // Save tokens and navigate
//                    _authState.value = AuthState.Success("Login successful")
//                }
//                is Result.Error -> {
//                    _authState.value = AuthState.Error(result.error.toErrorMessage())
//                }
//            }
//        }
//    }
//
//    fun resendOtp(email: String, username: String) {
//        viewModelScope.launch {
//            when (val result = sendOtpUseCase(email, username)) {
//                is Result.Success -> {
//                    _authState.value = AuthState.Success("OTP resent successfully")
//                }
//                is Result.Error -> {
//                    _authState.value = AuthState.Error(result.error.toErrorMessage())
//                }
//            }
//        }
//    }
//
//    private fun validateRegistration(
//        username: String,
//        email: String,
//        password: String,
//        confirmPassword: String,
//        imageBytes: ByteArray?
//    ): Boolean {
//        when {
//            username.isEmpty() -> {
//                _authState.value = AuthState.Error("Username cannot be empty")
//                return false
//            }
//            username.length > 15 -> {
//                _authState.value = AuthState.Error("Username cannot exceed 15 characters")
//                return false
//            }
//            !isValidEmail(email) -> {
//                _authState.value = AuthState.Error("Invalid email format")
//                return false
//            }
//            !validatePassword(password) -> {
//                return false
//            }
//            password != confirmPassword -> {
//                _authState.value = AuthState.Error("Passwords do not match")
//                return false
//            }
//            imageBytes == null -> {
//                _authState.value = AuthState.Error("Please select a profile picture")
//                return false
//            }
//        }
//        return true
//    }
//
//    private fun isValidEmail(email: String): Boolean {
//        return email.contains("@") && email.contains(".")
//    }
//
//    private fun clearErrors() {
//        _emailError.value = null
//        _usernameError.value = null
//        _passwordError.value = null
//        _authState.value = AuthState.Idle
//    }
//
//    fun resetState() {
//        _authState.value = AuthState.Idle
//    }
//
//    private fun DataError.Remote.toErrorMessage(): String {
//        return when (this) {
//            DataError.Remote.REQUEST_TIMEOUT -> "Request timed out. Please try again."
//            DataError.Remote.NO_INTERNET -> "No internet connection. Please check your network."
//            DataError.Remote.SERVER -> "Server error. Please try again later."
//            DataError.Remote.SERIALIZATION -> "Data error. Please try again."
//            DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests. Please wait a moment."
//            DataError.Remote.UNKNOWN -> "An unexpected error occurred. Please try again."
//        }
//    }
//}
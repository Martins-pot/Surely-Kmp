package com.sportmaster.surelykmp.core.domain.usecase

import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.core.data.model.AuthResponse


import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RegisterUserUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        imageBytes: ByteArray
    ): Result<Unit, DataError.Remote> {
        return authRepository.registerUser(username, email, password, imageBytes)
    }
}


@Serializable
data class JwtPayload(
    val user_id: String? = null,
    val username: String? = null,
    val image: String? = null,
    val exp: Long? = null
)

//class LoginUserUseCase(
//    private val authRepository: AuthRepositoryImpl,
//    private val userPreferences: UserPreferences
//) {
//    suspend operator fun invoke(
//        username: String,
//        email: String,
//        password: String,
//        deviceToken: String = ""
//    ): Result<Unit, DataError.Remote> {
//        return try {
//            println("=== LOGIN USECASE START ===")
//            println("LoginUserUseCase - Calling authRepository.login...")
//
//            when (val result = authRepository.login(username, email, password, deviceToken)) {
//                is Result.Success -> {
//                    val authResponse = result.data
//                    println("LoginUserUseCase - Login API SUCCESS")
//                    println("LoginUserUseCase - Access token: ${authResponse.accessToken.take(20)}...")
//
//                    // Save tokens
//                    println("LoginUserUseCase - Saving tokens...")
//                    userPreferences.accessToken = authResponse.accessToken
//                    userPreferences.refreshToken = authResponse.refreshToken
//
//                    // Decode JWT to get user info
//                    val jwtPayload = decodeJwtPayload(authResponse.accessToken)
//                    println("LoginUserUseCase - JWT decoded: ${jwtPayload?.username}")
//
//                    if (jwtPayload != null) {
//                        println("LoginUserUseCase - Saving user data...")
//                        userPreferences.username = jwtPayload.username
//                        userPreferences.userId = jwtPayload.user_id
//                        userPreferences.avatar = jwtPayload.image
//                    }
//
//                    // CRITICAL: Set logged in flag
//                    println("LoginUserUseCase - Setting isLoggedIn = TRUE")
//                    userPreferences.isLoggedIn = true
//
//                    // Verify saved data
//                    println("LoginUserUseCase - Verifying saved data:")
////                    userPreferences.debugPrintAll()
//
//                    println("=== LOGIN USECASE END - SUCCESS ===")
//                    Result.Success(Unit)
//                }
//                is Result.Error -> {
//                    println("=== LOGIN USECASE END - API ERROR ===")
//                    Result.Error(result.error)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("=== LOGIN USECASE END - EXCEPTION: ${e.message} ===")
//            Result.Error(DataError.Remote.UNKNOWN)
//        }
//    }
//
//    @OptIn(ExperimentalEncodingApi::class)
//    private fun decodeJwtPayload(token: String): JwtPayload? {
//        return try {
//            val parts = token.split(".")
//            if (parts.size != 3) {
//                println("LoginUserUseCase - Invalid JWT format")
//                return null
//            }
//
//            val payload = parts[1]
//            val paddedPayload = when (payload.length % 4) {
//                2 -> "$payload=="
//                3 -> "$payload="
//                else -> payload
//            }
//
//            val decodedBytes = Base64.decode(paddedPayload)
//            val decodedString = decodedBytes.decodeToString()
//
//            val json = Json { ignoreUnknownKeys = true }
//            json.decodeFromString<JwtPayload>(decodedString)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("LoginUserUseCase - JWT decode error: ${e.message}")
//            null
//        }
//    }
//}
class LoginUserUseCase(
    private val authRepository: AuthRepositoryImpl,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(
        username: String? = null,
        email: String? = null,
        password: String,
        deviceToken: String = ""
    ): Result<Unit, DataError.Remote> {
        return try {
            println("=== LOGIN USECASE START ===")
            println("LoginUserUseCase - Username: $username, Email: $email")

            when (val result = authRepository.login(username, email, password, deviceToken)) {
                is Result.Success -> {
                    val authResponse = result.data
                    println("LoginUserUseCase - Login API SUCCESS")
                    println("LoginUserUseCase - Access token: ${authResponse.accessToken.take(20)}...")

                    // Save tokens
                    println("LoginUserUseCase - Saving tokens...")
                    userPreferences.accessToken = authResponse.accessToken
                    userPreferences.refreshToken = authResponse.refreshToken

                    // Decode JWT to get user info
                    val jwtPayload = decodeJwtPayload(authResponse.accessToken)
                    println("LoginUserUseCase - JWT decoded: ${jwtPayload?.username}")

                    if (jwtPayload != null) {
                        println("LoginUserUseCase - Saving user data...")
                        userPreferences.username = jwtPayload.username
                        userPreferences.userId = jwtPayload.user_id
                        userPreferences.avatar = jwtPayload.image
                    }

                    // CRITICAL: Set logged in flag
                    println("LoginUserUseCase - Setting isLoggedIn = TRUE")
                    userPreferences.isLoggedIn = true

                    // Verify saved data
                    println("LoginUserUseCase - Verifying saved data:")
                    // userPreferences.debugPrintAll()

                    println("=== LOGIN USECASE END - SUCCESS ===")
                    Result.Success(Unit)
                }
                is Result.Error -> {
                    println("=== LOGIN USECASE END - API ERROR ===")
                    Result.Error(result.error)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("=== LOGIN USECASE END - EXCEPTION: ${e.message} ===")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeJwtPayload(token: String): JwtPayload? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                println("LoginUserUseCase - Invalid JWT format")
                return null
            }

            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> "$payload=="
                3 -> "$payload="
                else -> payload
            }

            val decodedBytes = Base64.decode(paddedPayload)
            val decodedString = decodedBytes.decodeToString()

            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<JwtPayload>(decodedString)
        } catch (e: Exception) {
            e.printStackTrace()
            println("LoginUserUseCase - JWT decode error: ${e.message}")
            null
        }
    }
}
class VerifyOtpUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        email: String,
        otp: String
    ): Result<Unit, DataError.Remote> {
        return authRepository.verifyOtp(email, otp)
    }
}



class SendOtpUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        email: String,
        username: String
    ): Result<Unit, DataError.Remote> {
        return authRepository.sendOtp(email, username)
    }
}


class CheckEmailAvailabilityUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(email: String): Result<Boolean, DataError.Remote> {
        return authRepository.checkEmailAvailability(email)
    }
}



class CheckUsernameAvailabilityUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(username: String): Result<Boolean, DataError.Remote> {
        return authRepository.checkUsernameAvailability(username)
    }
}

class CheckEmailExistsUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(email: String): Result<Boolean, DataError.Remote> {
        return authRepository.checkEmailExists(email)
    }
}

class SendOtpByEmailUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(email: String): Result<Unit, DataError.Remote> {
        return authRepository.sendOtpByEmail(email)
    }
}
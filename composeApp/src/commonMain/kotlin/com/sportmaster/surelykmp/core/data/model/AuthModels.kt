package com.sportmaster.surelykmp.core.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("device_token") val deviceToken: String = ""
)

@Serializable
data class LoginRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("device_token") val deviceToken: String
)

@Serializable
data class OtpVerificationRequest(
    val email: String,
    val otp: String
)

@Serializable
data class OtpSendRequest(
    val email: String,
    val username: String,
    val subject: String,
    val template_name: String,
    val tag: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val avatar: String? = null
)

@Serializable
data class UserResponse(
    @SerialName("username") val userName: String?,
    @SerialName("_id") val userId: String?,
    val email: String?,
    val avatar: String?,
    @SerialName("subscription_status") val subscriptionStatus: String?
)

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
    object OtpSent : AuthState()
}
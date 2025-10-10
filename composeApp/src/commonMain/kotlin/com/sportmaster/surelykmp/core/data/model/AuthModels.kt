package com.sportmaster.surelykmp.core.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val username: String = "",
    val email: String = "",
    val password: String,
    @SerialName("device_token") val deviceToken: String = ""
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class UpdatePasswordRequest(
    val password: String
)

@Serializable
data class SubscriptionInfo(
    @SerialName("subscription_start_time") val startTime: String?,
    @SerialName("subscription_end_time") val endTime: String?,
    @SerialName("subscription_status") val status: String?,
    @SerialName("subscription_product_id") val productId: String?,
    @SerialName("subscription_token") val token: String?
)

// Local storage models
data class UserSession(
    val username: String,
    val email: String,
    val avatar: String,
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val isRemembered: Boolean = false
)
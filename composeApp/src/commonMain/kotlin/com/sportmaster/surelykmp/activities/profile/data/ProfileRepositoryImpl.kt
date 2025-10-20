package com.sportmaster.surelykmp.activities.profile.data

import com.russhwolf.settings.Settings
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class User(
    @SerialName("username") val userName: String? = null,
    @SerialName("_id") val userId: String? = null,
    val password: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    @SerialName("cloudinary_id") val cloudinaryId: String? = null,
    @SerialName("subscription_start_time") val subscriptionStartTime: String? = null,
    @SerialName("subscription_end_time") val subscriptionEndTime: String? = null,
    @SerialName("subscription_status") val subscriptionStatus: String? = null,
    @SerialName("subscription_product_id") val subscriptionProductId: String? = null,
    @SerialName("subscription_token") val subscriptionToken: String? = null,
    @SerialName("is_subscribed") val isSubscribed: Boolean? = null
)

class ProfileRepositoryImpl(
    private val httpClient: HttpClient,
    private val userPreferences: UserPreferences
) : ProfileRepository {

    private val apiService = CodesApiService(httpClient)
    companion object {
        private const val BASE_URL = "https://srv442638.hstgr.cloud"
    }

    override suspend fun fetchUserDetails(username: String): Result<User, DataError.Remote> {
        return try {
            val users: List<User> = httpClient.get("$BASE_URL/user") {
                parameter("page", "1")
            }.body()

            val user = users.find {
                it.userName?.equals(username, ignoreCase = true) == true
            }

            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(DataError.Remote.UNKNOWN) // Or create a specific error for user not found
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }


    }
    override suspend fun getUserByEmail(email: String): Result<User, DataError.Remote> {
        return apiService.getUserByEmail(email)
    }


    override suspend fun isUserLoggedIn(): Boolean {
        return userPreferences.isLoggedIn
    }

    override suspend fun getUsername(): String? {
        return userPreferences.username
    }

    override suspend fun getAvatar(): String? {
        return userPreferences.avatar
    }

    override suspend fun getUserId(): String? {
        return userPreferences.userId
    }

    override suspend fun getSubscriptionStatus(): String? {
        return userPreferences.subscriptionStatus
    }

    override suspend fun getSubscriptionEndTime(): String? {
        return userPreferences.subscriptionEndTime
    }

    override suspend fun getRememberMe(): Boolean {
        return userPreferences.rememberMe
    }

    override suspend fun saveUserData(user: User) {
        userPreferences.apply {
            username = user.userName
            avatar = user.avatar
            userId = user.userId
            email = user.email
            subscriptionStatus = user.subscriptionStatus
            subscriptionStartTime = user.subscriptionStartTime
            subscriptionEndTime = user.subscriptionEndTime
            subscriptionProductId = user.subscriptionProductId
            subscriptionToken = user.subscriptionToken
        }
    }

    override suspend fun clearUserData() {
        userPreferences.clearUserData()
    }

    override suspend fun clearTokens() {
        userPreferences.accessToken = null
        userPreferences.refreshToken = null
    }

    override suspend fun clearSubscriptionData() {
        userPreferences.clearSubscriptionData()
    }

    override suspend fun getEmail(): String? {
        return userPreferences.email
    }

    override suspend fun getStoredPassword(): String? {
        // You'll need to add password storage to UserPreferences
        // For now, return null or implement if you have password storage
        return null
    }

    override suspend fun updatePassword(userId: String, newPassword: String): Result<Unit, DataError.Remote> {
        return apiService.updatePassword(userId, newPassword)
    }

    override suspend fun updateUserProfile(userId: String, newPassword: String?, imageBytes: ByteArray?): Result<Unit, DataError.Remote> {
        val result = apiService.updateUserProfile(userId, newPassword, imageBytes)

        // Update local storage if password was changed
        if (newPassword != null && result is Result.Success) {
            updateStoredPassword(newPassword)
        }

        return result
    }

    override suspend fun deleteUserAccount(userId: String): Result<Unit, DataError.Remote> {
        val result = apiService.deleteUserAccount(userId)
        if (result is Result.Success) {
            clearAllUserData()
        }
        return result
    }

    override suspend fun updateStoredPassword(password: String) {
        // Implement if you have password storage in UserPreferences
    }

    override suspend fun clearAllUserData() {
        userPreferences.clearAll()
    }
}

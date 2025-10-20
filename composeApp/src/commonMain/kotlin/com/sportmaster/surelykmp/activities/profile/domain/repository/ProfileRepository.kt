package com.sportmaster.surelykmp.activities.profile.domain.repository

import com.sportmaster.surelykmp.activities.profile.data.User
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.data.remote.DataError

interface ProfileRepository {
    suspend fun fetchUserDetails(username: String): Result<User, DataError.Remote>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getUsername(): String?
    suspend fun getAvatar(): String?
    suspend fun getUserId(): String?
    suspend fun getSubscriptionStatus(): String?
    suspend fun getSubscriptionEndTime(): String?
    suspend fun getRememberMe(): Boolean
    suspend fun saveUserData(user: User)
    suspend fun clearUserData()
    suspend fun clearTokens()
    suspend fun clearSubscriptionData()

    suspend fun getEmail(): String?
    suspend fun getStoredPassword(): String?
    suspend fun updatePassword(userId: String, newPassword: String): Result<Unit, DataError.Remote>
    suspend fun updateUserProfile(userId: String, newPassword: String?, imageBytes: ByteArray?): Result<Unit, DataError.Remote>
    suspend fun deleteUserAccount(userId: String): Result<Unit, DataError.Remote>
    suspend fun updateStoredPassword(password: String)
    suspend fun clearAllUserData()
    suspend fun getUserByEmail(email: String): Result<User, DataError.Remote>
}

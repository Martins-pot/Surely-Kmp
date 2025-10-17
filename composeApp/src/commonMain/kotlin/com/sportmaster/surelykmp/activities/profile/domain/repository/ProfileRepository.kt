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
}
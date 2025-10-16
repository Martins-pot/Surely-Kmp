package com.sportmaster.surelykmp.activities.profile.domain.repository

interface ProfileRepository {
    suspend fun fetchUserDetails(): Result<User>
    suspend fun getSubscriptionStatus(): Result<Boolean>
}
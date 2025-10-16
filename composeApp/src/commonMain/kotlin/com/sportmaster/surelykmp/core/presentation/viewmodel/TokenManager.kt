package com.sportmaster.surelykmp.core.presentation.viewmodel

// commonMain/kotlin/com/sportmaster/surelykmp/core/presentation/viewmodel/TokenManager.kt

//
//import com.russhwolf.settings.Settings
//import com.russhwolf.settings.get
//import com.russhwolf.settings.set
//
//object TokenManager {
//    private val settings: Settings = Settings()
//
//    // Keys for storing data
//    private const val KEY_ACCESS_TOKEN = "access_token"
//    private const val KEY_REFRESH_TOKEN = "refresh_token"
//    private const val KEY_USER_ID = "user_id"
//    private const val KEY_USERNAME = "username"
//    private const val KEY_EMAIL = "email"
//    private const val KEY_AVATAR = "avatar"
//    private const val KEY_IS_LOGGED_IN = "is_logged_in"
//    private const val KEY_REMEMBER_ME = "remember_me"
//    private const val KEY_IS_PREMIUM = "is_premium"
//    private const val KEY_SUBSCRIPTION_START = "subscription_start"
//    private const val KEY_SUBSCRIPTION_END = "subscription_end"
//    private const val KEY_SUBSCRIPTION_STATUS = "subscription_status"
//
//    // Token Management
//    fun saveAccessToken(token: String) {
//        settings[KEY_ACCESS_TOKEN] = token
//    }
//
//    fun getAccessToken(): String? {
//        return settings.getStringOrNull(KEY_ACCESS_TOKEN)
//    }
//
//    fun saveRefreshToken(token: String) {
//        settings[KEY_REFRESH_TOKEN] = token
//    }
//
//    fun getRefreshToken(): String? {
//        return settings.getStringOrNull(KEY_REFRESH_TOKEN)
//    }
//
//    // User Data Management
//    fun saveUserData(
//        userId: String,
//        username: String,
//        email: String,
//        avatar: String?
//    ) {
//        settings[KEY_USER_ID] = userId
//        settings[KEY_USERNAME] = username
//        settings[KEY_EMAIL] = email
//        avatar?.let { settings[KEY_AVATAR] = it }
//    }
//
//    fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)
//    fun getUsername(): String? = settings.getStringOrNull(KEY_USERNAME)
//    fun getEmail(): String? = settings.getStringOrNull(KEY_EMAIL)
//    fun getAvatar(): String? = settings.getStringOrNull(KEY_AVATAR)
//
//    // Login State Management
//    fun setLoggedIn(isLoggedIn: Boolean) {
//        settings[KEY_IS_LOGGED_IN] = isLoggedIn
//    }
//
//    fun isLoggedIn(): Boolean {
//        return settings.getBoolean(KEY_IS_LOGGED_IN, false)
//    }
//
//    fun setRememberMe(remember: Boolean) {
//        settings[KEY_REMEMBER_ME] = remember
//    }
//
//    fun shouldRememberMe(): Boolean {
//        return settings.getBoolean(KEY_REMEMBER_ME, false)
//    }
//
//    // Subscription Management
//    fun saveSubscriptionData(
//        isPremium: Boolean,
//        startTime: Long,
//        endTime: Long,
//        status: String
//    ) {
//        settings[KEY_IS_PREMIUM] = isPremium
//        settings[KEY_SUBSCRIPTION_START] = startTime
//        settings[KEY_SUBSCRIPTION_END] = endTime
//        settings[KEY_SUBSCRIPTION_STATUS] = status
//    }
//
//    fun isPremium(): Boolean {
//        return settings.getBoolean(KEY_IS_PREMIUM, false)
//    }
//
//    fun getSubscriptionStart(): Long {
//        return settings.getLong(KEY_SUBSCRIPTION_START, 0L)
//    }
//
//    fun getSubscriptionEnd(): Long {
//        return settings.getLong(KEY_SUBSCRIPTION_END, 0L)
//    }
//
//    fun getSubscriptionStatus(): String? {
//        return settings.getStringOrNull(KEY_SUBSCRIPTION_STATUS)
//    }
//
//    fun isSubscriptionValid(): Boolean {
//        val status = getSubscriptionStatus()
//        val endTime = getSubscriptionEnd()
//        val currentTime = System.currentTimeMillis()
//
//        return status == "valid" && endTime > currentTime
//    }
//
//    // Clear all data
//    fun clearAll() {
//        settings.clear()
//    }
//
//    // Clear tokens only (soft logout)
//    fun clearTokens() {
//        settings.remove(KEY_ACCESS_TOKEN)
//        settings.remove(KEY_REFRESH_TOKEN)
//        settings[KEY_IS_LOGGED_IN] = false
//    }
//
//    // Logout (respects remember me)
//    fun logout() {
//        if (shouldRememberMe()) {
//            // Keep user credentials but clear tokens
//            clearTokens()
//        } else {
//            // Clear everything
//            clearAll()
//        }
//    }
//}
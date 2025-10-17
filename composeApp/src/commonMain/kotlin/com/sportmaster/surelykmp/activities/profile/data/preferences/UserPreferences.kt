package com.sportmaster.surelykmp.activities.profile.data.preferences


import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class UserPreferences(private val settings: Settings) {

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_REMEMBER_ME = "rememberMe"
        private const val KEY_SUBSCRIPTION_STATUS = "subscription_status"
        private const val KEY_SUBSCRIPTION_START_TIME = "subscription_start_time"
        private const val KEY_SUBSCRIPTION_END_TIME = "subscription_end_time"
        private const val KEY_SUBSCRIPTION_PRODUCT_ID = "subscription_product_id"
        private const val KEY_SUBSCRIPTION_TOKEN = "subscription_token"
    }

    // Login Status
    var isLoggedIn: Boolean
        get() = settings[KEY_LOGGED_IN, "no"] == "yes"
        set(value) = settings.putString(KEY_LOGGED_IN, if (value) "yes" else "no")

    // User Info
    var username: String?
        get() = settings.getStringOrNull(KEY_USERNAME)
        set(value) {
            if (value != null) settings[KEY_USERNAME] = value
            else settings.remove(KEY_USERNAME)
        }

    var email: String?
        get() = settings.getStringOrNull(KEY_EMAIL)
        set(value) {
            if (value != null) settings[KEY_EMAIL] = value
            else settings.remove(KEY_EMAIL)
        }

    var userId: String?
        get() = settings.getStringOrNull(KEY_USER_ID)
        set(value) {
            if (value != null) settings[KEY_USER_ID] = value
            else settings.remove(KEY_USER_ID)
        }

    var avatar: String?
        get() = settings.getStringOrNull(KEY_AVATAR)
        set(value) {
            if (value != null) settings[KEY_AVATAR] = value
            else settings.remove(KEY_AVATAR)
        }

    // Tokens
    var accessToken: String?
        get() = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        set(value) {
            if (value != null) settings[KEY_ACCESS_TOKEN] = value
            else settings.remove(KEY_ACCESS_TOKEN)
        }

    var refreshToken: String?
        get() = settings.getStringOrNull(KEY_REFRESH_TOKEN)
        set(value) {
            if (value != null) settings[KEY_REFRESH_TOKEN] = value
            else settings.remove(KEY_REFRESH_TOKEN)
        }

    var rememberMe: Boolean
        get() = settings[KEY_REMEMBER_ME, false]
        set(value) = settings.putBoolean(KEY_REMEMBER_ME, value)

    // Subscription
    var subscriptionStatus: String?
        get() = settings.getStringOrNull(KEY_SUBSCRIPTION_STATUS)
        set(value) {
            if (value != null) settings[KEY_SUBSCRIPTION_STATUS] = value
            else settings.remove(KEY_SUBSCRIPTION_STATUS)
        }

    var subscriptionStartTime: String?
        get() = settings.getStringOrNull(KEY_SUBSCRIPTION_START_TIME)
        set(value) {
            if (value != null) settings[KEY_SUBSCRIPTION_START_TIME] = value
            else settings.remove(KEY_SUBSCRIPTION_START_TIME)
        }

    var subscriptionEndTime: String?
        get() = settings.getStringOrNull(KEY_SUBSCRIPTION_END_TIME)
        set(value) {
            if (value != null) settings[KEY_SUBSCRIPTION_END_TIME] = value
            else settings.remove(KEY_SUBSCRIPTION_END_TIME)
        }

    var subscriptionProductId: String?
        get() = settings.getStringOrNull(KEY_SUBSCRIPTION_PRODUCT_ID)
        set(value) {
            if (value != null) settings[KEY_SUBSCRIPTION_PRODUCT_ID] = value
            else settings.remove(KEY_SUBSCRIPTION_PRODUCT_ID)
        }

    var subscriptionToken: String?
        get() = settings.getStringOrNull(KEY_SUBSCRIPTION_TOKEN)
        set(value) {
            if (value != null) settings[KEY_SUBSCRIPTION_TOKEN] = value
            else settings.remove(KEY_SUBSCRIPTION_TOKEN)
        }

    // Utility methods
    fun clearUserData() {
        isLoggedIn = false
        username = null
        email = null
        userId = null
        avatar = null
        accessToken = null
        refreshToken = null
        rememberMe = false
    }

    fun clearSubscriptionData() {
        subscriptionStatus = null
        subscriptionStartTime = null
        subscriptionEndTime = null
        subscriptionProductId = null
        subscriptionToken = null
    }

    fun clearAll() {
        settings.clear()
    }

    fun isSubscriptionValid(): Boolean {
        if (subscriptionStatus != "valid") return false

        return try {
            val endTime = subscriptionEndTime?.toLongOrNull() ?: return false
            val currentTime = System.currentTimeMillis()
            endTime > currentTime
        } catch (e: Exception) {
            false
        }
    }
}

// Extension function for easier Settings usage
fun Settings.getStringOrNull(key: String): String? {
    return if (hasKey(key)) getString(key, "") else null
}
package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels

import android.content.Context
import android.content.SharedPreferences

actual class PreferencesManager(private val context: Context) {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("premium_prefs", Context.MODE_PRIVATE)

    actual fun putLong(key: String, value: Long) {
        sharedPrefs.edit().putLong(key, value).apply()
    }

    actual fun getLong(key: String, defaultValue: Long): Long {
        return sharedPrefs.getLong(key, defaultValue)
    }

    actual fun remove(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }

    actual fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }

    actual fun putString(key: String, value: String) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return sharedPrefs.getString(key, defaultValue)
    }
}

package com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels

import platform.Foundation.NSUserDefaults

actual class PreferencesManager {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun putLong(key: String, value: Long) {
        userDefaults.setInteger(value, key)
    }

    actual fun getLong(key: String, defaultValue: Long): Long {
        val value = userDefaults.integerForKey(key)
        return if (value == 0L && userDefaults.objectForKey(key) == null) defaultValue else value
    }

    actual fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    actual fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, key)
    }

    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.boolForKey(key)
        } else defaultValue
    }

    actual fun putString(key: String, value: String) {
        userDefaults.setObject(value, key)
    }

    actual fun getString(key: String, defaultValue: String?): String? {
        return userDefaults.stringForKey(key) ?: defaultValue
    }
}
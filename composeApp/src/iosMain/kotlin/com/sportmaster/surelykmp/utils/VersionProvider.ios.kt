package com.sportmaster.surelykmp.utils

import platform.Foundation.NSBundle

actual class AppVersionProvider {

    actual fun getVersionName(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
            ?: "1.0.0"
    }

    actual fun getPlatform(): String = "ios"
}
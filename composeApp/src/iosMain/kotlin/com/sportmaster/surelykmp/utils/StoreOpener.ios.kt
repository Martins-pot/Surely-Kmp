package com.sportmaster.surelykmp.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openStoreForUpdate() {
    // Replace with your actual App Store ID
    val appId = "YOUR_APP_STORE_ID" // e.g., "123456789"
    val urlString = "https://apps.apple.com/app/id$appId"

    NSURL.URLWithString(urlString)?.let { url ->
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}
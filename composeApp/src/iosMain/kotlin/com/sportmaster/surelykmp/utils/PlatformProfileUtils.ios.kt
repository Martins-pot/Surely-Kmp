package com.sportmaster.surelykmp.utils

import platform.UIKit.*
import platform.Foundation.*

class IosPlatformUtils : PlatformUtils {
    override fun openEmail(email: String, subject: String, body: String) {
        val urlString = "mailto:$email?subject=${subject.encodeURL()}&body=${body.encodeURL()}"
        val url = NSURL.URLWithString(urlString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    override fun shareText(text: String, title: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }

    override fun openAppInStore(packageName: String) {
        val appStoreUrl = NSURL.URLWithString("https://apps.apple.com/app/id$packageName")
        if (appStoreUrl != null) {
            UIApplication.sharedApplication.openURL(appStoreUrl)
        }
    }

    override fun copyToClipboard(text: String, label: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    private fun String.encodeURL(): String {
        return this.addingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet
        ) ?: this
    }
}

actual fun getPlatformUtils(): PlatformUtils {
    TODO("Not yet implemented")
}
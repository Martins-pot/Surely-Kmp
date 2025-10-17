package com.sportmaster.surelykmp.utils

interface PlatformUtils {
    fun openEmail(email: String, subject: String = "", body: String = "")
    fun shareText(text: String, title: String = "Share")
    fun openAppInStore(packageName: String)
    fun copyToClipboard(text: String, label: String = "Copied Text")
}

expect fun getPlatformUtils(): PlatformUtils
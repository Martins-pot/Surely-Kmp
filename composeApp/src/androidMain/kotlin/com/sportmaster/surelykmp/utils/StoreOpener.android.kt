package com.sportmaster.surelykmp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

// Store the context globally for easy access
private lateinit var applicationContext: Context

fun initializeStoreOpener(context: Context) {
    applicationContext = context.applicationContext
}

actual fun openStoreForUpdate() {
    val packageName = applicationContext.packageName

    try {
        // Try to open Play Store app
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$packageName")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback to browser if Play Store not installed
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }
}

//Uri.parse("https://play.google.com/store/apps/details?id=$packageName")

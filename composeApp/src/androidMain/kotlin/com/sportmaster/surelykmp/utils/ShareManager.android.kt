package com.sportmaster.surelykmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import android.content.Context
import android.content.Intent

actual class ShareManager(private val context: Context) {
    actual fun shareText(text: String, title: String?) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            // Add FLAG_ACTIVITY_NEW_TASK if context is not an Activity
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val shareTitle = title ?: "Share Code"
        val chooserIntent = Intent.createChooser(shareIntent, shareTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(chooserIntent)
    }
}
@Composable
actual fun rememberShareManager(): ShareManager {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { ShareManager(context) }
}
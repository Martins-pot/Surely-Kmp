package com.sportmaster.surelykmp.utils

import androidx.compose.runtime.Composable


expect class ShareManager {
    fun shareText(text: String, title: String? = null)
}

@Composable
expect fun rememberShareManager(): ShareManager
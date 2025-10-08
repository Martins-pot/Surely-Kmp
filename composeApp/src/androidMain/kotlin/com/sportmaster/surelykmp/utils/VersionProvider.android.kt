package com.sportmaster.surelykmp.utils


import android.content.Context
import android.content.pm.PackageManager

actual class AppVersionProvider(private val context: Context) {

    actual fun getVersionName(): String {
        return  "1.0.1"
//        return try {
////            val packageInfo = context.packageManager.getPackageInfo(
////                context.packageName,
////                0
////            )
////            packageInfo.versionName ?: "1.0.1"
//        } catch (e: PackageManager.NameNotFoundException) {
//            "1.0.1"
//        }
    }

    actual fun getPlatform(): String = "android"
}
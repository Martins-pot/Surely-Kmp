package com.sportmaster.surelykmp.utils

expect class AppVersionProvider {
    fun getVersionName(): String
    fun getPlatform(): String
}
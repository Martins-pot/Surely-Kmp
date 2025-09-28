package com.sportmaster.surelykmp.utils

expect class UnityAdsManager {
    fun initializeAds(gameId: String)
    fun loadRewardedAd(placementId: String)
    fun showRewardedAd(
        placementId: String,
        onAdCompleted: () -> Unit,
        onAdFailed: (String) -> Unit
    )
    fun isAdReady(placementId: String): Boolean

    fun loadBannerAd(placementId: String)
    fun isBannerReady(placementId: String): Boolean
}
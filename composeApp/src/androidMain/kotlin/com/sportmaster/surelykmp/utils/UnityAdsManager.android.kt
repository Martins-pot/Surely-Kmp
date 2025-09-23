package com.sportmaster.surelykmp.utils

import android.app.Activity
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity

actual class UnityAdsManager(private val context: Context) {

    companion object {
        lateinit var currentActivityProvider: () -> Activity
    }

    // Track load state for each placement
    private val adReadyMap = mutableMapOf<String, Boolean>()

    actual fun initializeAds(gameId: String) {
        UnityAds.initialize(context, gameId, testMode = true,
            object : IUnityAdsInitializationListener {
                override fun onInitializationComplete() {

                }
                override fun onInitializationFailed(
                    error: UnityAds.UnityAdsInitializationError?,
                    message: String?
                ) { Log.e("UnityAds", "Init failed: $message") }
            }
        )
    }

    actual fun loadRewardedAd(placementId: String) {
        adReadyMap[placementId] = false   // reset
        UnityAds.load(placementId, object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(pid: String) {
                adReadyMap[pid] = true
            }

            override fun onUnityAdsFailedToLoad(
                pid: String,
                error: UnityAds.UnityAdsLoadError?,
                message: String?
            ) {
                adReadyMap[pid] = false
                Log.e("UnityAds", "Load failed: $message")
            }
        })
    }

    actual fun showRewardedAd(
        placementId: String,
        onAdCompleted: () -> Unit,
        onAdFailed: (String) -> Unit
    ) {
        if (adReadyMap[placementId] != true) {
            onAdFailed("Ad not ready")
            return
        }

        val activity = currentActivityProvider()
        UnityAds.show(
            activity,
            placementId,
            object : IUnityAdsShowListener {
                override fun onUnityAdsShowStart(pid: String) {}
                override fun onUnityAdsShowClick(pid: String) {}
                override fun onUnityAdsShowComplete(
                    pid: String,
                    state: UnityAds.UnityAdsShowCompletionState
                ) {
                    adReadyMap[pid] = false  // consume
                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED)
                        onAdCompleted()
                    else
                        onAdFailed("Ad skipped or not completed")
                }
                override fun onUnityAdsShowFailure(
                    pid: String,
                    error: UnityAds.UnityAdsShowError?,
                    message: String?
                ) {
                    adReadyMap[pid] = false
                    onAdFailed(message ?: "Ad show failed")
                }
            }
        )
    }

    //  replacement for the old isReady
    actual fun isAdReady(placementId: String): Boolean {
        return adReadyMap[placementId] == true
    }
}

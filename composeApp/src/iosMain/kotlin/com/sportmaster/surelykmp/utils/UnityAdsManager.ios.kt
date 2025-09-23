package com.sportmaster.surelykmp.utils

import UnityAds

actual class UnityAdsManager {

    actual fun initializeAds(gameId: String) {
        UnityAds.initialize(gameId, testMode = false) { error in
                if let error = error {
                    print("Unity Ads initialization failed: \(error.localizedDescription)")
                } else {
                    print("Unity Ads initialized successfully")
                }
        }
    }

    actual fun loadRewardedAd(placementId: String) {
        UnityAds.load(placementId) { placementId, error in
            if let error = error {
                print("Failed to load ad: \(error.localizedDescription)")
            } else {
                print("Ad loaded successfully for placement: \(placementId)")
            }
        }
    }

    actual fun showRewardedAd(
        placementId: String,
        onAdCompleted: () -> Unit,
        onAdFailed: (String) -> Unit
    ) {
        guard UnityAds.isReady(placementId) else {
            onAdFailed("Ad not ready")
            return
        }

        UnityAds.show(placementId) { placementId, state in
            switch state {
                case .completed:
                onAdCompleted()
                case .skipped, .failed:
                onAdFailed("Ad was skipped or failed")
                @unknown default:
                onAdFailed("Unknown ad completion state")
            }
        }
    }

    actual fun isAdReady(placementId: String): Boolean {
        return UnityAds.isReady(placementId)
    }
}
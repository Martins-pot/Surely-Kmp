package com.sportmaster.surelykmp.activities.freecodes.presentation.screens

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

@Composable
actual fun PlatformBannerAd(
    placementId: String,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            // Cast context to Activity only if you really need it
            val bannerView = BannerView(context as Activity, placementId, UnityBannerSize(320, 50))
            bannerView.setListener(object : BannerView.IListener {
                override fun onBannerLoaded(bannerAdView: BannerView?) {
                    // No need to call show(); Unity shows it automatically
                    Log.d("UnityBanner", "Banner loaded successfully")
                }

                override fun onBannerShown(bannerAdView: BannerView?) {

                }

                override fun onBannerClick(bannerAdView: BannerView?) {
                    Log.d("UnityBanner", "Banner clicked")
                }

                override fun onBannerFailedToLoad(
                    bannerAdView: BannerView?,
                    errorInfo: BannerErrorInfo?
                ) {
                    Log.e("UnityBanner", "Failed to load banner: ${errorInfo?.errorMessage}")
                }

                override fun onBannerLeftApplication(bannerAdView: BannerView?) {
                    Log.d("UnityBanner", "User left app from banner")
                }
            })
            bannerView.load()   // This starts loading and automatically shows the banner
            bannerView          // Return the view to Compose
        },
        modifier = modifier
    )
}

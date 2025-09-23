package com.sportmaster.surelykmp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.sportmaster.surelykmp.di.initKoin
import com.sportmaster.surelykmp.di.platformModule
import com.sportmaster.surelykmp.di.sharedModule
import com.sportmaster.surelykmp.utils.UnityAdsManager
import org.koin.android.ext.koin.androidContext


class SurelyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin{
            androidContext(this@SurelyApp)
            modules(
                sharedModule,
                platformModule
            )

        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                UnityAdsManager.currentActivityProvider = { activity }
            }
            override fun onActivityPaused(activity: Activity) { /* no-op */ }

            // other callbacks can be left empty
            override fun onActivityCreated(a: Activity, s: Bundle?) {}
            override fun onActivityStarted(a: Activity) {}
            override fun onActivityStopped(a: Activity) {}
            override fun onActivitySaveInstanceState(a: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(a: Activity) {}
        })
    }
    }

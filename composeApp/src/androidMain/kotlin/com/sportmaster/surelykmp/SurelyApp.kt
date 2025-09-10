package com.sportmaster.surelykmp

import android.app.Application
import com.sportmaster.surelykmp.di.initKoin
import com.sportmaster.surelykmp.di.platformModule
import com.sportmaster.surelykmp.di.sharedModule


class SurelyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin{
            modules(
                sharedModule,
                platformModule
            )

        }
    }
}
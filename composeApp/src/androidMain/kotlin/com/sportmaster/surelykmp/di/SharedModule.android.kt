package com.sportmaster.surelykmp.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PreferencesManager
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ImageRepository
import com.sportmaster.surelykmp.repository.ImageRepositoryImpl
import com.sportmaster.surelykmp.utils.AppVersionProvider
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single { PreferencesManager(androidApplication()) }
    single { UnityAdsManager(androidApplication()) }
    single { AppVersionProvider(androidContext()) }
    single<ImageRepository> { ImageRepositoryImpl(get<Context>()) }

    single<Settings> {
        val context: Context = get()
        val sharedPreferences = context.getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )
        SharedPreferencesSettings(sharedPreferences)
    }
}
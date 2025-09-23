package com.sportmaster.surelykmp.di

import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PreferencesManager
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single { PreferencesManager(androidApplication()) }
    single { UnityAdsManager(androidApplication()) }
}
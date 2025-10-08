package com.sportmaster.surelykmp.di



import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PreferencesManager
import com.sportmaster.surelykmp.utils.AppVersionProvider
import com.sportmaster.surelykmp.utils.UnityAdsManager
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single { PreferencesManager() }
    single { UnityAdsManager() }
    single { AppVersionProvider() }
}
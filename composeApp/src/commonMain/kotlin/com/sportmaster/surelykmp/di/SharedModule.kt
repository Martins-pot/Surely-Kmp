package com.sportmaster.surelykmp.di


import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
//import com.sportmaster.surelykmp.activities.freecodes.presentation.screens.codes.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.activities.matches.data.api.MatchesApiService
import com.sportmaster.surelykmp.activities.matches.data.repository.MatchesRepository
import com.sportmaster.surelykmp.activities.matches.domain.GetMatchesUseCase
import com.sportmaster.surelykmp.activities.matches.domain.GetPredictionUseCase
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.core.data.HttpClientFactory
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.repository.VersionCheckRepository
import com.sportmaster.surelykmp.core.domain.usecase.CheckAppVersionUseCase
import com.sportmaster.surelykmp.core.presentation.viewmodel.VersionCheckViewModel
import com.sportmaster.surelykmp.utils.AppVersionProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

//val sharedModule = module {
//    // Network
//    single { HttpClientFactory.create(get()) }
//    single { CodesApiService(get()) }
//    single { MatchesApiService(get()) }
//
//    // Repository
//    single { CodesRepository(get()) }
//    single { MatchesRepository(get()) }
//
//    // Use Cases
//    single { GetCodesUseCase(get()) }
//    single { GetMatchesUseCase(get()) }
//    single { GetPredictionUseCase(get())}
//
//
//    // ViewModels
//    viewModel { CodesViewModel(get(), get()) }
//    viewModel { PremiumCodesViewModel(get(), get(), get()) }
////    viewModel { MatchesViewModel(get(), get()) }
//    viewModel {
//        MatchesViewModel(
//            getMatchesUseCase = get(),
//            getPredictionUseCase = get(),
//            unityAdsManager = get(),
//            preferencesManager = get()
//        )
//    }
//}

val sharedModule = module {
    // Network
    single { HttpClientFactory.create(get()) }
    single { CodesApiService(get()) }
    single { MatchesApiService(get()) }

    // Repository
    single { CodesRepository(get()) }
    single { MatchesRepository(get()) }
    single { VersionCheckRepository(get()) }

    // Use Cases
    single { GetCodesUseCase(get()) }
    single { GetMatchesUseCase(get()) }
    single { GetPredictionUseCase(get()) }
    single { CheckAppVersionUseCase(get()) }

    // ViewModels
    viewModel { CodesViewModel(get(), get()) }
    viewModel { PremiumCodesViewModel(get(), get(), get()) }
    viewModel {
        MatchesViewModel(
            getMatchesUseCase = get(),
            getPredictionUseCase = get(),
            unityAdsManager = get(),
            preferencesManager = get()
        )
    }

    // Version Check ViewModel
    viewModel {
        val versionProvider: AppVersionProvider = get()
        VersionCheckViewModel(
            checkAppVersionUseCase = get(),
            currentVersion = versionProvider.getVersionName(),
            platform = versionProvider.getPlatform()
        )
    }
}
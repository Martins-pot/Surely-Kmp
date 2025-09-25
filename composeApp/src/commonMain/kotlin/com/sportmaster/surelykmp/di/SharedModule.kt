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
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // Network
    single { HttpClientFactory.create(get()) }
    single { CodesApiService(get()) }
    single { MatchesApiService(get()) }

    // Repository
    single { CodesRepository(get()) }
    single { MatchesRepository(get()) }

    // Use Cases
    single { GetCodesUseCase(get()) }
    single { GetMatchesUseCase(get()) }
    single { GetPredictionUseCase(get())}


    // ViewModels
    viewModel { CodesViewModel(get()) }
    viewModel { PremiumCodesViewModel(get(), get(), get()) }
//    viewModel { MatchesViewModel(get(), get()) }
    viewModel {
        MatchesViewModel(
            getMatchesUseCase = get(),
            getPredictionUseCase = get(),
            unityAdsManager = get(),
            preferencesManager = get()
        )
    }
}
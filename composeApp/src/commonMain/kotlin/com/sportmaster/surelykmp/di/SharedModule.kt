package com.sportmaster.surelykmp.di


import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
//import com.sportmaster.surelykmp.activities.freecodes.presentation.screens.codes.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
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

    // Repository
    single { CodesRepository(get()) }

    // Use Cases
    single { GetCodesUseCase(get()) }

    // ViewModels
    viewModel { CodesViewModel(get()) }
}
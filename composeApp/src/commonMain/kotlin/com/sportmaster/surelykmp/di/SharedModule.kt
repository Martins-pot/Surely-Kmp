// commonMain/kotlin/com/sportmaster/surelykmp/di/SharedModule.kt
package com.sportmaster.surelykmp.di

import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.activities.matches.data.api.MatchesApiService
import com.sportmaster.surelykmp.activities.matches.data.repository.MatchesRepository
import com.sportmaster.surelykmp.activities.matches.domain.GetMatchesUseCase
import com.sportmaster.surelykmp.activities.matches.domain.GetPredictionUseCase
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.RegisterViewModel
import com.sportmaster.surelykmp.core.data.HttpClientFactory
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.repository.VersionCheckRepository
import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl
import com.sportmaster.surelykmp.core.domain.usecase.CheckAppVersionUseCase
import com.sportmaster.surelykmp.core.domain.usecase.RegisterUserUseCase
import com.sportmaster.surelykmp.core.domain.usecase.LoginUserUseCase
import com.sportmaster.surelykmp.core.domain.usecase.VerifyOtpUseCase
import com.sportmaster.surelykmp.core.domain.usecase.SendOtpUseCase
import com.sportmaster.surelykmp.core.domain.usecase.CheckEmailAvailabilityUseCase
import com.sportmaster.surelykmp.core.domain.usecase.CheckUsernameAvailabilityUseCase
import com.sportmaster.surelykmp.core.presentation.viewmodel.VersionCheckViewModel
import com.sportmaster.surelykmp.utils.AppVersionProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // ==================== Network ====================
    single { HttpClientFactory.create(get()) }
    single { CodesApiService(get()) }
    single { MatchesApiService(get()) }

    // ==================== Repositories ====================
    single { CodesRepository(get()) }
    single { MatchesRepository(get()) }
    single { VersionCheckRepository(get()) }
    single { AuthRepositoryImpl(get()) }

    // ==================== Use Cases ====================
    // Codes Use Cases
    single { GetCodesUseCase(get()) }
    single { GetMatchesUseCase(get()) }
    single { GetPredictionUseCase(get()) }
    single { CheckAppVersionUseCase(get()) }

    // Auth Use Cases
    single { RegisterUserUseCase(get()) }
    single { LoginUserUseCase(get()) }
    single { VerifyOtpUseCase(get()) }
    single { SendOtpUseCase(get()) }
    single { CheckEmailAvailabilityUseCase(get()) }
    single { CheckUsernameAvailabilityUseCase(get()) }

    // ==================== ViewModels ====================
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

    viewModel {
        RegisterViewModel(
            registerUserUseCase = get(),
            loginUserUseCase = get(),
            verifyOtpUseCase = get(),
            sendOtpUseCase = get(),
            checkEmailAvailabilityUseCase = get(),
            checkUsernameAvailabilityUseCase = get(),
            imageRepository = get()
        )
    }

    viewModel {
        val versionProvider: AppVersionProvider = get()
        VersionCheckViewModel(
            checkAppVersionUseCase = get(),
            currentVersion = versionProvider.getVersionName(),
            platform = versionProvider.getPlatform()
        )
    }
}
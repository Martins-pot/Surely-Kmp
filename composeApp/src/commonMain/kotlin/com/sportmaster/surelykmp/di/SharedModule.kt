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
import com.sportmaster.surelykmp.activities.profile.data.ProfileRepositoryImpl
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.activities.profile.domain.repository.ProfileRepository
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsViewModel
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordViewModel
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ProfileViewModel
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ForgotPasswordViewModel
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
import com.sportmaster.surelykmp.core.domain.usecase.CheckEmailExistsUseCase
import com.sportmaster.surelykmp.core.domain.usecase.CheckUsernameAvailabilityUseCase
import com.sportmaster.surelykmp.core.domain.usecase.SendOtpByEmailUseCase
import com.sportmaster.surelykmp.core.presentation.viewmodel.VersionCheckViewModel
import com.sportmaster.surelykmp.utils.AppVersionProvider
import com.sportmaster.surelykmp.utils.PlatformUtils
import com.sportmaster.surelykmp.utils.getPlatformUtils
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // ==================== Network ====================
    // ==================== Platform ====================
    single<PlatformUtils> { getPlatformUtils() }

    // ==================== Settings ====================
    // Platform-specific Settings instance (expect from platformModule)
    single { UserPreferences(get()) }

    // ==================== Network ====================
    single { HttpClientFactory.create(get()) }
    single { CodesApiService(get()) }
    single { MatchesApiService(get()) }

    // ==================== Repositories ====================
    single { CodesRepository(get()) }
    single { MatchesRepository(get()) }
    single { VersionCheckRepository(get()) }
    single { AuthRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    // ==================== Use Cases ====================
    // Codes Use Cases
    single { GetCodesUseCase(get()) }
    single { GetMatchesUseCase(get()) }
    single { GetPredictionUseCase(get()) }
    single { CheckAppVersionUseCase(get()) }

    // Auth Use Cases
    single { RegisterUserUseCase(get()) }
    single { LoginUserUseCase(get() ,get()) }
    single { VerifyOtpUseCase(get()) }
    single { SendOtpUseCase(get()) }
    single { CheckEmailAvailabilityUseCase(get()) }
    single { CheckUsernameAvailabilityUseCase(get()) }
    single { CheckEmailExistsUseCase(get()) }
    single { SendOtpByEmailUseCase(get()) }

    // ==================== ViewModels ====================
    viewModel { CodesViewModel(get(), get(), get()) }
    viewModel { PremiumCodesViewModel(get(), get(), get(), get()) }
    viewModel { AccountDetailsViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get(), get()) }

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
            imageRepository = get(),
            userPreferences = get()
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

    viewModel { ProfileViewModel(get(), get()) }
}
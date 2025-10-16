package com.sportmaster.surelykmp.core.domain.usecase

import com.sportmaster.surelykmp.core.data.model.AuthResponse


import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class RegisterUserUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        imageBytes: ByteArray
    ): Result<Unit, DataError.Remote> {
        return authRepository.registerUser(username, email, password, imageBytes)
    }
}

//// commonMain/kotlin/com/sportmaster/surelykmp/core/domain/usecase/LoginUserUseCase.kt
//package com.sportmaster.surelykmp.core.domain.usecase
//
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import com.sportmaster.surelykmp.core.data.model.AuthResponse
//import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class LoginUserUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        deviceToken: String = ""
    ): Result<AuthResponse, DataError.Remote> {
        return authRepository.login(username, email, password, deviceToken)
    }
}

//// commonMain/kotlin/com/sportmaster/surelykmp/core/domain/usecase/VerifyOtpUseCase.kt
//package com.sportmaster.surelykmp.core.domain.usecase
//
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class VerifyOtpUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        email: String,
        otp: String
    ): Result<Unit, DataError.Remote> {
        return authRepository.verifyOtp(email, otp)
    }
}

//// commonMain/kotlin/com/sportmaster/surelykmp/core/domain/usecase/SendOtpUseCase.kt
//package com.sportmaster.surelykmp.core.domain.usecase
//
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class SendOtpUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(
        email: String,
        username: String
    ): Result<Unit, DataError.Remote> {
        return authRepository.sendOtp(email, username)
    }
}

//// commonMain/kotlin/com/sportmaster/surelykmp/core/domain/usecase/CheckEmailAvailabilityUseCase.kt
//package com.sportmaster.surelykmp.core.domain.usecase
//
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class CheckEmailAvailabilityUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(email: String): Result<Boolean, DataError.Remote> {
        return authRepository.checkEmailAvailability(email)
    }
}

//// commonMain/kotlin/com/sportmaster/surelykmp/core/domain/usecase/CheckUsernameAvailabilityUseCase.kt
//package com.sportmaster.surelykmp.core.domain.usecase
//
//import com.sportmaster.surelykmp.core.data.remote.Result
//import com.sportmaster.surelykmp.core.data.remote.DataError
//import com.sportmaster.surelykmp.core.data.repository.AuthRepositoryImpl

class CheckUsernameAvailabilityUseCase(private val authRepository: AuthRepositoryImpl) {
    suspend operator fun invoke(username: String): Result<Boolean, DataError.Remote> {
        return authRepository.checkUsernameAvailability(username)
    }
}
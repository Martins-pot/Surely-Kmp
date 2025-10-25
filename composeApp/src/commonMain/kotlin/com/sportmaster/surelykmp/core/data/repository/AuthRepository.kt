package com.sportmaster.surelykmp.core.data.repository

import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.model.AuthResponse
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.core.data.remote.DataError

class AuthRepository(private val apiService: CodesApiService) {

    suspend fun checkEmailAvailability(email: String): Result<Boolean, DataError.Remote> {
        return apiService.checkEmailAvailability(email)
    }

    suspend fun checkUsernameAvailability(username: String): Result<Boolean, DataError.Remote> {
        return apiService.checkUsernameAvailability(username)
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        imageBytes: ByteArray
    ): Result<Unit, DataError.Remote> {
        return apiService.registerUser(username, email, password, imageBytes)
    }

    suspend fun sendOtp(email: String, username: String): Result<Unit, DataError.Remote> {
        return apiService.sendOtp(email, username)
    }

    suspend fun verifyOtp(email: String, otp: String): Result<Unit, DataError.Remote> {
        return apiService.verifyOtp(email, otp)
    }

    suspend fun sendOtpByEmail(email: String): Result<Unit, DataError.Remote> {
        return apiService.sendOtpByEmail(email)
    }

    suspend fun login(
        username: String,
        email: String,
        password: String,
        deviceToken: String = ""
    ): Result<AuthResponse, DataError.Remote> {
        return apiService.login(username, email, password, deviceToken)
    }
}
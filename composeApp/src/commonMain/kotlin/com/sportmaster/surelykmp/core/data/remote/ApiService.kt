package com.sportmaster.surelykmp.core.data.remote

import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.profile.data.User
import com.sportmaster.surelykmp.core.data.model.AuthResponse
import com.sportmaster.surelykmp.core.data.model.LoginRequest
import com.sportmaster.surelykmp.core.data.model.OtpSendRequest
import com.sportmaster.surelykmp.core.data.model.UserResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.parameters

class CodesApiService(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://srv442638.hstgr.cloud"
        private const val CODES_ENDPOINT = "$BASE_URL/codes/"
    }

    suspend fun getAllCodes(): Result<List<Code>, DataError.Remote> {
        return safeCall<List<Code>> {
            client.get(CODES_ENDPOINT)
        }
    }

    suspend fun getAllUsers(): Result<List<User>, DataError.Remote> {
        return safeCall<List<User>> {
            client.get("$BASE_URL/user") {
                parameter("page", "1")
            }
        }
    }

    suspend fun checkEmailAvailability(email: String): Result<Boolean, DataError.Remote> {
        return try {
            val users: List<UserResponse> = client.get("$BASE_URL/user") {
                parameter("page", "1")
            }.body()
            val exists = users.any { it.email.equals(email, ignoreCase = true) }
            Result.Success(exists)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun checkUsernameAvailability(username: String): Result<Boolean, DataError.Remote> {
        return try {
            val users: List<UserResponse> = client.get("$BASE_URL/user") {
                parameter("page", "1")
            }.body()
            val exists = users.any { it.userName.equals(username, ignoreCase = true) }
            Result.Success(exists)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        imageBytes: ByteArray
    ): Result<Unit, DataError.Remote> {
        return try {
            client.submitFormWithBinaryData(
                url = "$BASE_URL/user/register",
                formData = formData {
                    append("username", username)
                    append("email", email)
                    append("password", password)
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                    })
                }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun sendOtp(email: String, username: String): Result<Unit, DataError.Remote> {
        return try {
            val request = OtpSendRequest(
                email = email,
                username = username,
                subject = "Verification Code",
                template_name = "otp_template",
                tag = "registration"
            )
            client.post("$BASE_URL/otp/send") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<Unit, DataError.Remote> {
        return try {
            client.submitForm(
                url = "$BASE_URL/user/verify",
                formParameters = parameters {
                    append("email", email)
                    append("otp", otp)
                }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun login(
        username: String,
        email: String,
        password: String,
        deviceToken: String
    ): Result<AuthResponse, DataError.Remote> {
        return try {
            val request = LoginRequest(username, email, password, deviceToken)
            val response: AuthResponse = client.post("$BASE_URL/user/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            Result.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getUserByUsername(username: String): Result<User?, DataError.Remote> {
        return try {
            val result = getAllUsers()
            when (result) {
                is Result.Success -> {
                    val user = result.data.find { it.userName.equals(username, ignoreCase = true) }
                    Result.Success(user)
                }
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        newPassword: String? = null,
        imageBytes: ByteArray? = null
    ): Result<Unit, DataError.Remote> {
        return try {
            if (imageBytes != null && newPassword != null) {
                // Update both image and password
                client.submitFormWithBinaryData(
                    url = "$BASE_URL/user/update/$userId",
                    formData = formData {
                        append("password", newPassword)
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                        })
                    }
                )
            } else if (newPassword != null) {
                // Update only password - FIXED: Use parameters instead of formData
                client.submitForm(
                    url = "$BASE_URL/user/update/$userId",
                    formParameters = parameters {
                        append("password", newPassword)
                    }
                )
            } else if (imageBytes != null) {
                // Update only image
                client.submitFormWithBinaryData(
                    url = "$BASE_URL/user/update/$userId",
                    formData = formData {
                        append("image", imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                        })
                    }
                )
            } else {
                // Nothing to update
                return Result.Success(Unit)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun updatePassword(
        userId: String,
        newPassword: String
    ): Result<Unit, DataError.Remote> {
        return try {
            // FIXED: Use parameters instead of formData for simple form submissions
            client.submitForm(
                url = "$BASE_URL/user/update/$userId",
                formParameters = parameters {
                    append("password", newPassword)
                }
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun deleteUserAccount(userId: String): Result<Unit, DataError.Remote> {
        return try {
            client.delete("$BASE_URL/user/delete/$userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getCurrentUser(): Result<User, DataError.Remote> {
        return safeCall<User> {
            client.get("$BASE_URL/user/me")
        }
    }
}
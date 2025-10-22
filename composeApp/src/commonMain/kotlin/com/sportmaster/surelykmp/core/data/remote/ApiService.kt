package com.sportmaster.surelykmp.core.data.remote

import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.data.model.Comment
import com.sportmaster.surelykmp.activities.freecodes.data.model.Rating
import com.sportmaster.surelykmp.activities.profile.data.User
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.core.data.model.AuthResponse
import com.sportmaster.surelykmp.core.data.model.LoginRequest
import com.sportmaster.surelykmp.core.data.model.OtpSendRequest
import com.sportmaster.surelykmp.core.data.model.UserResponse
import com.sportmaster.surelykmp.utils.bearerAuth
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CodesApiService(
    private val client: HttpClient
) : KoinComponent {

    private val userPreferences: UserPreferences by inject()
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

    suspend fun addComment(
        codeId: String,
        commentText: String
    ): Result<Comment, DataError.Remote> {
        return try {
            val response: Comment = executeWithAuthRetry { token ->
                val httpResponse = client.post("$BASE_URL/codes/add_comment/$codeId") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(mapOf("comment" to commentText))
                }

                // Manually check response status
                if (httpResponse.status == HttpStatusCode.Unauthorized) {
                    throw ClientRequestException(httpResponse, "Token expired")
                }

                httpResponse.body()
            }
            Result.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Add comment error: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun addRating(
        codeId: String,
        rating: Double
    ): Result<Unit, DataError.Remote> {
        return try {
            executeWithAuthRetry { token ->
                val httpResponse = client.post("$BASE_URL/codes/add_rating/$codeId") {
                    contentType(ContentType.Application.Json)
                    bearerAuth(token)
                    setBody(Rating(rating = rating))
                }

                // Manually check response status
                if (httpResponse.status == HttpStatusCode.Unauthorized) {
                    throw ClientRequestException(httpResponse, "Token expired")
                }

                httpResponse.body<Unit>()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Add rating error: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    private suspend fun <T> executeWithAuthRetry(
        request: suspend (String) -> T
    ): T {
        var accessToken = userPreferences.accessToken
            ?: throw Exception("Not authenticated")

        return try {
            println("Making request with token...")
            // First attempt with current token
            request(accessToken)
        } catch (e: ClientRequestException) {
            println("Caught ClientRequestException: ${e.response.status} - ${e.message}")
            if (e.response.status == HttpStatusCode.Unauthorized) {
                println("Token expired, refreshing...")
                val newToken = refreshAccessToken()
                println("Token refreshed, retrying request...")
                // Retry with new token
                request(newToken)
            } else {
                println("Request failed with status: ${e.response.status}")
                throw e
            }
        } catch (e: Exception) {
            println("Caught generic exception: ${e::class.simpleName} - ${e.message}")
            // If it's a generic exception but we suspect it's auth-related, try refresh
            if (e.message?.contains("401") == true ||
                e.message?.contains("unauthorized", ignoreCase = true) == true ||
                e.message?.contains("token", ignoreCase = true) == true) {
                println("Auth-related exception detected, attempting token refresh...")
                val newToken = refreshAccessToken()
                println("Token refreshed, retrying request...")
                request(newToken)
            } else {
                throw e
            }
        }
    }

    private suspend fun refreshAccessToken(): String {
        val refreshToken = userPreferences.refreshToken
            ?: throw Exception("No refresh token available")

        try {
            println("Calling refresh endpoint with refresh token: ${refreshToken.take(10)}...")

            // Create a simple data class for the refresh response
            @Serializable
            data class RefreshResponse(
                @SerialName("access_token") val accessToken: String? = null,
                @SerialName("token_type") val tokenType: String? = null
            )

            val response: RefreshResponse = client.post("$BASE_URL/user/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("token" to refreshToken))
            }.body()

            println("Refresh successful! New access token received")

            response.accessToken?.let {
                userPreferences.accessToken = it
                println("New access token saved: ${it.take(10)}...")
            }

            // Note: The refresh token remains the same since it's not returned in the response
            // Only update the access token

            return response.accessToken
                ?: throw Exception("No access token in refresh response")

        } catch (e: Exception) {
            println("Token refresh failed: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            userPreferences.clearUserData()
            throw Exception("Session expired. Please log in again.")
        }
    }


    suspend fun getCodeById(codeId: String): Result<Code, DataError.Remote> {
        return try {
            val allCodesResult = getAllCodes()
            when (allCodesResult) {
                is Result.Success -> {
                    val code = allCodesResult.data.find { it._id == codeId }
                    if (code != null) {
                        Result.Success(code)
                    } else {
                        Result.Error(DataError.Remote.UNKNOWN)
                    }
                }
                is Result.Error -> allCodesResult
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    suspend fun getUserByEmail(email: String): Result<User, DataError.Remote> {
        return try {
            val result = getAllUsers()
            when (result) {
                is Result.Success -> {
                    val user = result.data.find {
                        it.email.equals(email, ignoreCase = true)
                    }
                    if (user != null) {
                        Result.Success(user)
                    } else {
                        Result.Error(DataError.Remote.USER_NOT_FOUND)
                    }
                }
                is Result.Error -> result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
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
        username: String? = null,
        email: String? = null,
        password: String,
        deviceToken: String = ""
    ): Result<AuthResponse, DataError.Remote> {
        return try {
            // Validate that either username or email is provided, but not both
            if (username == null && email == null) {
                return Result.Error(DataError.Remote.INVALID_CREDENTIALS)
            }

            if (username != null && email != null) {
                return Result.Error(DataError.Remote.INVALID_CREDENTIALS)
            }

            val request = LoginRequest(
                username = username,
                email = email,
                password = password,
                deviceToken = deviceToken
            )

            println("Login request - Username: $username, Email: $email")

            val response: AuthResponse = client.post("$BASE_URL/user/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            Result.Success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Login error: ${e.message}")
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
//    suspend fun login(
//        username: String,
//        email: String,
//        password: String,
//        deviceToken: String
//    ): Result<AuthResponse, DataError.Remote> {
//        return try {
//            val request = LoginRequest(username, email, password, deviceToken)
//            val response: AuthResponse = client.post("$BASE_URL/user/login") {
//                contentType(ContentType.Application.Json)
//                setBody(request)
//            }.body()
//            Result.Success(response)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Remote.UNKNOWN)
//        }
//    }

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
                client.submitForm(
                    url = "$BASE_URL/user/update/$userId",
                    formParameters = parameters {
                        append("password", newPassword)
                    }
                )
            } else if (imageBytes != null) {
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
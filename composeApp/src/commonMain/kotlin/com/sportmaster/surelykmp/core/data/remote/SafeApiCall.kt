package com.sportmaster.surelykmp.core.data.remote


import io.ktor.client.call.*
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.*
import io.ktor.serialization.*
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

sealed interface DataError {
    enum class Remote : DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        TOO_MANY_REQUESTS,
        USER_NOT_FOUND,
        INVALID_CREDENTIALS,
        UNKNOWN
    }
}

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E>(val error: E) : Result<Nothing, E>
}

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        in 400..499 -> Result.Error(DataError.Remote.INVALID_CREDENTIALS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}
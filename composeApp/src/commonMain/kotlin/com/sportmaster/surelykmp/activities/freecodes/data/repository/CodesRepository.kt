package com.sportmaster.surelykmp.activities.freecodes.data.repository



import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result

class CodesRepository(
    private val apiService: CodesApiService
) {
    suspend fun getAllCodes(): Result<List<Code>, DataError.Remote> {
        return apiService.getAllCodes()
    }
}
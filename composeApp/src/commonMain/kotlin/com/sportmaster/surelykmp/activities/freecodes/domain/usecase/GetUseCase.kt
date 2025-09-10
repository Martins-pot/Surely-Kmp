package com.sportmaster.surelykmp.activities.freecodes.domain.usecase

import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.core.data.remote.DataError
import com.sportmaster.surelykmp.core.data.remote.Result

class GetCodesUseCase(
    private val repository: CodesRepository
) {
    suspend fun execute(sport: Sport): Result<List<Code>, DataError.Remote> {
        return when (val result = repository.getAllCodes()) {
            is Result.Success -> {
                val filteredCodes = result.data.filter {
                    it.sport!!.lowercase() == sport.apiValue
                }
                Result.Success(filteredCodes)
            }
            is Result.Error -> result
        }
    }
}
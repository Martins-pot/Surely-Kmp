package com.sportmaster.surelykmp.activities.freecodes.domain.usecase

import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport


class GetCodesUseCase(private val repository: CodesRepository) {
    suspend fun execute(sport: Sport): List<Code> {
        return repository.getAllCodes()
            .filter { it.sport.lowercase() == sport.apiValue }
    }
}
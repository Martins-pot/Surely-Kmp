package com.sportmaster.surelykmp.di

import com.sportmaster.surelykmp.activities.freecodes.data.repository.CodesRepository
import com.sportmaster.surelykmp.activities.freecodes.domain.usecase.GetCodesUseCase
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel


object AppModule {
    private val codesRepository by lazy { CodesRepository() }
    private val getCodesUseCase by lazy { GetCodesUseCase(codesRepository) }

    fun provideCodesViewModel(): CodesViewModel {
        return CodesViewModel(getCodesUseCase)
    }
}
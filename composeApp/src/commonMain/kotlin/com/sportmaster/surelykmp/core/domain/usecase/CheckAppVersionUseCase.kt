package com.sportmaster.surelykmp.core.domain.usecase

import com.sportmaster.surelykmp.core.data.model.VersionCheckResponse
import com.sportmaster.surelykmp.core.data.repository.VersionCheckRepository

class CheckAppVersionUseCase(
    private val repository: VersionCheckRepository
) {
    suspend operator fun invoke(
        currentVersion: String,
        platform: String
    ): Result<VersionCheckResponse> {
        return repository.checkVersion(currentVersion, platform)
    }
}
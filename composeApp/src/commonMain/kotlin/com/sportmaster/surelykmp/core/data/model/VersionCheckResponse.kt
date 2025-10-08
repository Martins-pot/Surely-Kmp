package com.sportmaster.surelykmp.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VersionCheckResponse(
    val latest_version: String,
    val current_version: String,
    val needs_update: Boolean,
    val force_update: Boolean
)
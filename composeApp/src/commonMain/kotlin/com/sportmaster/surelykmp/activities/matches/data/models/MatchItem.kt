package com.sportmaster.surelykmp.activities.matches.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatchItem(
    @SerialName("_id") val id: String,
    @SerialName("home_team") val homeTeam: String,
    @SerialName("home_team_image") val homeTeamImage: String,
    @SerialName("away_team") val awayTeam: String,
    @SerialName("away_team_image") val awayTeamImage: String,
    val league: String,
    val country: String,
    val sport: String,
    @SerialName("match_date") val matchDate: String,
    @SerialName("createdAt") val createdAt: String,
    val prediction: String? = null
)
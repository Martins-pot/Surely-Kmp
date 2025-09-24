package com.sportmaster.surelykmp.activities.matches.data.models

enum class SportMatch(val displayName: String, val apiName: String) {
    FOOTBALL("Football", "football"),
    BASKETBALL("Basketball", "basketball"),
    TENNIS("Tennis", "tennis")
}

data class PredictionItem(
    val title: String,
    val prediction: String
)
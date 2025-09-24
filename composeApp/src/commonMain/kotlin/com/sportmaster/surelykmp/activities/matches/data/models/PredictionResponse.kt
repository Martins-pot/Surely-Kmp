package com.sportmaster.surelykmp.activities.matches.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PredictionResponse(
    val message: String,
    val prediction: Prediction
)

@Serializable
data class Prediction(
    @SerialName("outcome_prediction") val outcomePrediction: String? = null,
    @SerialName("offsides_prediction") val offsidesPrediction: String? = null,
    @SerialName("possession_prediction") val possessionPrediction: String? = null,
    @SerialName("discipline_prediction") val disciplinePrediction: String? = null,
    @SerialName("passing_accuracy_prediction") val passingAccuracyPrediction: String? = null,
    @SerialName("shooting_prediction") val shootingPrediction: String? = null,
    @SerialName("corner_prediction") val cornerPrediction: String? = null,
    // Basketball predictions
    @SerialName("points_prediction") val pointsPrediction: String? = null,
    @SerialName("three_pointers_prediction") val threePointersPrediction: String? = null,
    @SerialName("rebounds_prediction") val reboundsPrediction: String? = null,
    @SerialName("foul_prediction") val foulPrediction: String? = null,
    @SerialName("assists_prediction") val assistsPrediction: String? = null,
    @SerialName("free_throws_prediction") val freeThrowsPrediction: String? = null,
    @SerialName("steals_prediction") val stealsPrediction: String? = null,
    @SerialName("blocks_prediction") val blocksPrediction: String? = null,
    // Tennis predictions
    @SerialName("aces_prediction") val acesPrediction: String? = null,
    @SerialName("double_faults_prediction") val doubleFaultsPrediction: String? = null,
    @SerialName("break_points_prediction") val breakPointsPrediction: String? = null,
    @SerialName("rally_prediction") val rallyPrediction: String? = null,
    @SerialName("sets_prediction") val setsPrediction: String? = null
)
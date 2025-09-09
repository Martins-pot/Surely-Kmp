package com.sportmaster.surelykmp.activities.freecodes.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Code(
    val _id: String,
    val group_id: String? = null,
    val user: String? = null,
    val username: String,
    val user_id: String,
    val image: String? = null,
    val odds: Double,
    val rating: Double,
    val telegram: Boolean = false,
    val text: String,
    val sport: String
)

//data class Codess(
//    @Json(name = "_id") val id: String?,
//    @Json(name = "group_id") val groupId: String?,
//    @Json(name = "user") val user: String?,
//    @Json(name = "username") val username: String?,
//    @Json(name = "user_id") val userId: String?,
//    @Json(name = "source_accuracy") val accuracy: Int?,
//    @Json(name = "platform") val platform: String?,
//    @Json(name = "odds") val odds: Double,
//    @Json(name = "rating") val rating: Double,
//    @Json(name = "telegram") val telegram: Boolean,
//    @Json(name = "is_expensive") val isExpensive: Boolean,
//    @Json(name = "code_type") val sportType: String?,
//    @Json(name = "text") val text: String,
//    @Json(name = "createdAt") val createdAt: String?,
//    @Json(name = "ratings") val ratings: List<Double>,
//    @Json(name = "comments") val comments: MutableList<Comment>,
//    @Json(name = "expiration_date") val expirationDate: String?,
//    @Json(name = "country") val country: String?,
//    @Json(name = "team1") val team1: String?,
//    @Json(name = "team2") val team2: String?
//
//
//
//)
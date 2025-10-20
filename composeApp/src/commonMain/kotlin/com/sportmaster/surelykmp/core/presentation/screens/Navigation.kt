package com.sportmaster.surelykmp.core.presentation.screens

sealed class Screen(val route: String) {
    object FreeCodes : Screen("free")
    object PremiumCodes : Screen("premium")
    object Profile : Screen("profile")
    object Account : Screen("account")
    object Matches : Screen("matches")
    object AiPredictions : Screen("predictions")
    object Subscription : Screen("subscription")
    object Login : Screen("login")
    object Register : Screen("register")
    object AccountDetails : Screen("acccountdetails")
    object ChangePassword : Screen("changepassword")
}
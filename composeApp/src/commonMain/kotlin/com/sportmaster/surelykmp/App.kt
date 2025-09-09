package com.sportmaster.surelykmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mertswork.footyreserve.ui.theme.FootyReserveTheme
import com.sportmaster.surelykmp.core.presentation.screens.MainScreen
import com.sportmaster.surelykmp.core.presentation.screens.Screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {

    val navController = rememberNavController()
    FootyReserveTheme {
        NavHost(
            navController = navController,
            startDestination = "main/free"
        ) {
            composable("main/{tab}") { backStackEntry ->
                val tab = backStackEntry.arguments?.getString("tab") ?: Screen.FreeCodes.route
                MainScreen(startDestination = tab)
            }

        }
    }
}
package com.sportmaster.surelykmp.core.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mertswork.footyreserve.ui.theme.BlackFaded
import com.mertswork.footyreserve.ui.theme.DarkGrayBackground
import com.sportmaster.surelykmp.activities.freecodes.presentation.screens.CodesScreen
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.activities.premiumcodes.presentation.screens.CodesScreenPremium
//import com.sportmaster.surelykmp.di.AppModule
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.background_texture
import surelykmp.composeapp.generated.resources.selected_free_codes
import surelykmp.composeapp.generated.resources.selected_matches
import surelykmp.composeapp.generated.resources.selected_premium_codes
import surelykmp.composeapp.generated.resources.selected_profile
import surelykmp.composeapp.generated.resources.unselected_free_codes
import surelykmp.composeapp.generated.resources.unselected_matches
import surelykmp.composeapp.generated.resources.unselected_premium_codes
import surelykmp.composeapp.generated.resources.unselected_profile

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val hasNews : Boolean,
    val badgeCount : Int? = null
)

@Composable
fun MainScreen(startDestination: String = Screen.FreeCodes.route){
    val navController = rememberNavController()
    val items = listOf(
        BottomNavigationItem(
            title = "free",
            selectedIcon = painterResource(Res.drawable.selected_free_codes),
            unselectedIcon = painterResource(Res.drawable.unselected_free_codes),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "premium",
            selectedIcon = painterResource(Res.drawable.selected_premium_codes),
            unselectedIcon = painterResource(Res.drawable.unselected_premium_codes),
            hasNews = false
        ),
        BottomNavigationItem(
            title = "matches",
            selectedIcon = painterResource(Res.drawable.selected_matches),
            unselectedIcon = painterResource(Res.drawable.unselected_matches),
            hasNews = false,
        )
    )
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //  Background image

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGrayBackground)
        )
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
            ,
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
//        Image(
//            painter = painterResource(Res.drawable.background_texture),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent, //  Let image show through
            contentColor = Color.White,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    //  Navigation Bar inside bottomBar
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
//                            .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                    ) {
                        items.forEachIndexed { index, item ->
                            val isSelected =
                                navController.currentDestination?.route == item.title.lowercase()

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    selectedItemIndex = index
                                    navController.navigate(item.title.lowercase()) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                },
                                label = { Text(text = item.title) },
                                alwaysShowLabel = false,
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            when {
                                                item.badgeCount != null -> {
                                                    Badge { Text(text = item.badgeCount.toString()) }
                                                }
                                                item.hasNews -> {
                                                    Badge()
                                                }
                                            }
                                        }
                                    ) {
                                        Image(
                                            painter = if (index == selectedItemIndex) {
                                                item.selectedIcon
                                            } else item.unselectedIcon,
                                            contentDescription = item.title,
                                            modifier = Modifier
                                                .sizeIn(24.dp, 24.dp, 30.dp, 30.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            //  Screen content above bottom bar
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.FreeCodes.route) { FreeCodes() }
                composable(Screen.Matches.route) { FreeCodes() }
                composable(Screen.PremiumCodes.route) { PremiumCodes() }
            }
        }
    }


}


@Composable
 fun FreeCodes(){

     val viewModel : CodesViewModel = koinInject()
//    val viewModel = remember { AppModule.provideCodesViewModel() }
    CodesScreen(viewModel = viewModel)
 }

@Composable
fun PremiumCodes() {
    val viewModelPremium: PremiumCodesViewModel = koinInject()
    CodesScreenPremium(viewModel = viewModelPremium)
}
package com.sportmaster.surelykmp.core.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mertswork.footyreserve.ui.theme.BlackFaded
import com.mertswork.footyreserve.ui.theme.DarkGrayBackground
import com.sportmaster.surelykmp.activities.comment.presentation.screens.CommentScreen
import com.sportmaster.surelykmp.activities.comment.presentation.viewmodels.CommentViewModel
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.presentation.screens.CodesScreen
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.activities.matches.presentation.screens.AiPredictionsScreen
import com.sportmaster.surelykmp.activities.matches.presentation.screens.MatchesScreen
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.activities.premiumcodes.presentation.screens.CodesScreenPremium
import com.sportmaster.surelykmp.activities.profile.data.preferences.UserPreferences
import com.sportmaster.surelykmp.activities.profile.presentation.screens.AccountDetailsScreen
import com.sportmaster.surelykmp.activities.profile.presentation.screens.ChangePasswordScreen
import com.sportmaster.surelykmp.activities.profile.presentation.screens.ProfileScreen
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsAction
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsViewModel
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordAction
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordViewModel
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ProfileAction
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ProfileViewModel
import com.sportmaster.surelykmp.activities.register.presentation.screens.ForgotPasswordScreen
import com.sportmaster.surelykmp.activities.register.presentation.screens.RegisterScreen
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ForgotPasswordViewModel
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.RegisterViewModel
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
//import com.sportmaster.surelykmp.di.AppModule
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.background_texture
import surelykmp.composeapp.generated.resources.selected_account
import surelykmp.composeapp.generated.resources.selected_free_codes
import surelykmp.composeapp.generated.resources.selected_matches
import surelykmp.composeapp.generated.resources.selected_premium_codes
import surelykmp.composeapp.generated.resources.selected_profile
import surelykmp.composeapp.generated.resources.unselected_account
import surelykmp.composeapp.generated.resources.unselected_free_codes
import surelykmp.composeapp.generated.resources.unselected_matches
import surelykmp.composeapp.generated.resources.unselected_premium_codes
import surelykmp.composeapp.generated.resources.unselected_profile
import com.sportmaster.surelykmp.core.data.remote.Result
import com.sportmaster.surelykmp.ui.theme.RushonGroundFamily

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val hasNews : Boolean,
    val badgeCount : Int? = null
)
fun NavController.navigateToAiPredictions(matchId: String) {
    navigate("${Screen.AiPredictions.route}/$matchId")
}

@Composable
fun MainScreen(startDestination: String = Screen.FreeCodes.route) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavigationItem(
            title = "Codes",
            selectedIcon = painterResource(Res.drawable.selected_free_codes),
            unselectedIcon = painterResource(Res.drawable.unselected_free_codes),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Expert",
            selectedIcon = painterResource(Res.drawable.selected_premium_codes),
            unselectedIcon = painterResource(Res.drawable.unselected_premium_codes),
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Ai-matches",
            selectedIcon = painterResource(Res.drawable.selected_matches),
            unselectedIcon = painterResource(Res.drawable.unselected_matches),
            hasNews = false,
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = painterResource(Res.drawable.selected_account),
            unselectedIcon = painterResource(Res.drawable.unselected_account),
            hasNews = false,
        )
    )

    fun getRouteForTitle(title: String): String {
        return when (title) {
            "Codes" -> Screen.FreeCodes.route      // "free"
            "Expert" -> Screen.PremiumCodes.route  // "premium" - NOT "expert"
            "Ai-matches" -> Screen.Matches.route   // "matches"
            "Profile" -> Screen.Account.route      // "account"
            else -> Screen.FreeCodes.route
        }
    }
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGrayBackground)
        )
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = Color.White,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        items.forEachIndexed { index, item ->
                            val route = getRouteForTitle(item.title) // Get the correct route
                            val isSelected = navController.currentDestination?.route == route

                            NavigationBarItem(
                                selected = index == selectedItemIndex,
                                onClick = {
                                    selectedItemIndex = index
                                    // FIX: Use the mapped route instead of item.title.lowercase()
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.title.replaceFirstChar { it.uppercase() },
                                        fontSize = 10.sp,
                                        fontFamily = RushonGroundFamily,
                                        color = if (index == selectedItemIndex) Color.Red else Color.White.copy(0.25f)
                                    )
                                },
                                alwaysShowLabel = true,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedTextColor = Color(0xFF9E2024).copy(.6f),
                                    unselectedTextColor = Color(0xFF7F7F7F).copy(.6f),
                                    selectedIconColor = Color.Transparent,
                                    unselectedIconColor = Color.Transparent,
                                    indicatorColor = Color.Transparent
                                ),
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            when {
                                                item.badgeCount != null -> {
                                                    Badge {
                                                        Text(
                                                            text = item.badgeCount.toString(),
                                                            fontSize = 10.sp
                                                        )
                                                    }
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
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ){ paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.FreeCodes.route) { FreeCodes(navController) }
                composable(Screen.Matches.route) { Matches(navController) }
                composable(Screen.PremiumCodes.route) { PremiumCodes(navController) }
                composable(Screen.Account.route)  { ProfileScreenContainer(navController) }


                composable(Screen.AccountDetails.route) { AccountDetailsScreenContainer(navController) }
                composable(
                        route = "${Screen.ChangePassword.route}/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
                ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                ChangePasswordScreenContainer(navController, email)
            }

// Keep the original route for logged-in users without email
                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreenContainer(navController, null)
                }

                composable(Screen.Register.route) {
                    RegisterScreenContainer(navController)
                }

                // Login screen (navigated to from Profile when in guest mode)
                composable(Screen.Login.route) {
                    LoginScreenContainer(navController)
                }


                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreenContainer(navController)
                }

                composable(
                    route = "${Screen.CommentScreen.route}/{codeId}",
                    arguments = listOf(navArgument("codeId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val codeId = backStackEntry.arguments?.getString("codeId") ?: ""
                    CommentScreenContainer(
                        codeId = codeId,
                        navController = navController
                    )
                }


                composable(
                    route = "${Screen.AiPredictions.route}/{matchId}",
                    arguments = listOf(navArgument("matchId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
                    AiPredictions(matchId = matchId, navController = navController)
                }
            }
        }
    }
}

@Composable
fun CommentScreenContainer(
    codeId: String,
    navController: NavController
) {
    val apiService: CodesApiService = koinInject()
    val userPreferences: UserPreferences = koinInject()
    var code by remember { mutableStateOf<Code?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Get actual user data from preferences
    val isLoggedIn = remember { userPreferences.isLoggedIn }
    val isSubscribed = remember { userPreferences.isSubscriptionValid() }
    val currentUser = remember { userPreferences.username ?: "" }
    val currentUserAvatar = remember { userPreferences.avatar }

    // Fetch the code when the screen loads
    LaunchedEffect(codeId) {
        isLoading = true
        error = null

        when (val result = apiService.getCodeById(codeId)) {
            is Result.Success -> {
                code = result.data
                isLoading = false
            }
            is Result.Error -> {
                error = "Failed to load code"
                isLoading = false
            }
        }
    }

    // Loading state
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    // Error state
    if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(error!!, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading = true
                        error = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Retry")
                }
            }
        }
        return
    }

    // Success state
    if (code != null) {
        val commentViewModel = remember(code) {
            CommentViewModel(apiService, code!!)
        }

        CommentScreen(
            viewModel = commentViewModel,
            isLoggedIn = isLoggedIn,
            isSubscribed = isSubscribed,
            currentUser = currentUser,
            currentUserAvatar = currentUserAvatar,
            onBackClick = { navController.popBackStack() },
            onCopyCode = { text ->
                // Basic implementation - replace with your platform utils
                println("Copy code: $text")
            },
            onShareCode = { text ->
                // Basic implementation - replace with your platform utils
                println("Share code: $text")
            },
            onSubscribeClick = {
                navController.navigate(Screen.Subscription.route)
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Code not found", color = Color.White)
        }
    }
}
//
@Composable
fun ForgotPasswordScreenContainer(navController: NavController) {
    val viewModel: ForgotPasswordViewModel = koinInject()

    ForgotPasswordScreen(
        navController = navController,
        viewModel = viewModel
    )
}



@Composable
fun AccountDetailsScreenContainer(navController: NavController) {
    val viewModel: AccountDetailsViewModel = koinInject()

    // Load user data when screen appears
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.onAction(AccountDetailsAction.LoadUserData)
    }

    AccountDetailsScreen(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun ChangePasswordScreenContainer(
    navController: NavController,
    email: String? = null
) {
    val viewModel: ChangePasswordViewModel = koinInject()

    // Pass email to viewModel if it exists (forgot password flow)
    LaunchedEffect(navController.currentBackStackEntry) {
        if (email != null) {
            viewModel.onAction(ChangePasswordAction.LoadUserDataWithEmail(email))
        } else {
            viewModel.onAction(ChangePasswordAction.LoadUserData)
        }
    }

    ChangePasswordScreen(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun FreeCodes(navController: NavController) {
    val viewModel: CodesViewModel = koinInject()

    CodesScreen(
        viewModel = viewModel,
        onCodeClick = { code ->
            navController.navigate("${Screen.CommentScreen.route}/${code._id}")
        }
    )
}

@Composable
fun PremiumCodes(navController: NavController) {
    val viewModelPremium: PremiumCodesViewModel = koinInject()

    CodesScreenPremium(
        viewModel = viewModelPremium,
        onCodeClick = { code ->
            navController.navigate("${Screen.CommentScreen.route}/${code._id}")
        }
    )
}

@Composable
fun Matches(navController: NavController) {
    val viewModel: MatchesViewModel = koinInject() // or viewModel()
    MatchesScreen(
        navController = navController,
        viewModel = viewModel
    )
}




@Composable
fun ProfileScreenContainer(navController: NavController) {
    val viewModel: ProfileViewModel = koinInject()

    // Reload profile whenever we navigate to this screen
    LaunchedEffect(navController.currentBackStackEntry) {
        println("ProfileScreenContainer - Triggering profile reload")
        viewModel.onAction(ProfileAction.OnLoadUserProfile)
    }

    ProfileScreen(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun RegisterScreenContainer(navController: NavController) {
    val viewModel: RegisterViewModel = koinInject()

    // Set initial mode to Register
    LaunchedEffect(Unit) {
        if (viewModel.isLoginMode.value) {
            viewModel.toggleMode()
        }
    }

    RegisterScreen(
        viewModel = viewModel,
        onNavigateBack = { navController.popBackStack() },
        onLoginSuccess = {
            println("RegisterScreenContainer - Registration/Login success, navigating to profile")
            // Navigate back to profile after successful registration/login
            navController.popBackStack(Screen.Account.route, inclusive = false)
        },
        navController = navController

    )
}

@Composable
fun LoginScreenContainer(navController: NavController) {
    val viewModel: RegisterViewModel = koinInject()

    // Set initial mode to Login
    LaunchedEffect(Unit) {
        if (!viewModel.isLoginMode.value) {
            viewModel.toggleMode()
        }
    }

    RegisterScreen(
        viewModel = viewModel,
        onNavigateBack = { navController.popBackStack() },
        onLoginSuccess = {
            println("LoginScreenContainer - Login success, navigating to profile")
            // Navigate back to profile after successful login
            navController.navigate(Screen.Account.route) {
                popUpTo(Screen.Account.route) { inclusive = true }
                launchSingleTop = true
            }
        },
        navController = navController

    )
}

@Composable
fun AiPredictions(
    matchId: String,
    navController: NavController
) {
    val viewModel: MatchesViewModel = koinInject()
    AiPredictionsScreen(
        matchId = matchId,
        onBackClick = {
            navController.popBackStack()
        },
        onSubscribeClick = {
            navController.navigate(Screen.Subscription.route)
        },
        viewModel = viewModel
    )
}
//@Composable
//fun AiPredictions(
//    matchId: String,
//    navController: NavController
//) {
//    val viewModel: MatchesViewModel = koinInject() // or viewModel()
//    AiPredictionsScreen(
//        matchId = matchId,
//        onBackClick = {
//            navController.popBackStack()
//        },
//        isSubscribed = false, // Get from your subscription manager
//        onSubscribeClick = {
//            navController.navigate(Screen.Subscription.route)
//        },
//        viewModel = viewModel
//    )
//}
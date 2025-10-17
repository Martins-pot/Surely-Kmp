package com.sportmaster.surelykmp.activities.profile.presentation.screens




import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.*
import com.sportmaster.surelykmp.core.presentation.screens.Screen
import com.sportmaster.surelykmp.ui.theme.NunitoBlackFamily
import com.sportmaster.surelykmp.ui.theme.NunitoBoldFamily
import com.sportmaster.surelykmp.ui.theme.NunitoMediumFamily
import com.sportmaster.surelykmp.ui.theme.NunitoSemiBoldFamily
import com.sportmaster.surelykmp.ui.theme.RushonGroundFamily
import com.sportmaster.surelykmp.utils.getPlatformUtils
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.*

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var showTermsSheet by remember { mutableStateOf(false) }
    var termsContent by remember { mutableStateOf("") }
    var termsTitle by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val platformUtils = remember { getPlatformUtils() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToLogin -> {
                    println("ProfileScreen - Navigating to Login")
                    navController.navigate(Screen.Login.route)
                }
                ProfileEvent.NavigateToRegister -> {
                    println("ProfileScreen - Navigating to Register")
                    navController.navigate(Screen.Register.route)
                }
                ProfileEvent.NavigateToSubscription -> navController.navigate(Screen.Subscription.route)
                ProfileEvent.NavigateToAccountDetails -> navController.navigate(Screen.AccountDetails.route)
                is ProfileEvent.OpenEmail -> {
                    platformUtils.openEmail(
                        email = event.email,
                        subject = "ForeSport Support",
                        body = ""
                    )
                }
                is ProfileEvent.ShareApp -> {
                    platformUtils.shareText(
                        text = event.appLink,
                        title = "Check out ForeSport!"
                    )
                }
                ProfileEvent.OpenRateApp -> {
                    platformUtils.openAppInStore("com.sportmaster.surelykmp")
                }
                is ProfileEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                is ProfileEvent.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    // Debug log for state changes
    LaunchedEffect(state.isLoggedIn, state.username) {
        println("ProfileScreen - State updated: isLoggedIn=${state.isLoggedIn}, username=${state.username}")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            ProfileHeader(
                isSubscribed = state.isSubscribed,
                onGetProClick = { viewModel.onAction(ProfileAction.OnGetProClick) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Content based on login state
            if (state.isLoggedIn) {
                println("ProfileScreen - Showing LoggedInContent")
                LoggedInContent(
                    state = state,
                    onAccountDetailsClick = { viewModel.onAction(ProfileAction.OnAccountDetailsClick) },
                    onContactUsClick = { viewModel.onAction(ProfileAction.OnContactUsClick) },
                    onShareAppClick = { viewModel.onAction(ProfileAction.OnShareAppClick) },
                    onRateAppClick = { viewModel.onAction(ProfileAction.OnRateAppClick) },
                    onLogoutClick = { viewModel.onAction(ProfileAction.OnLogoutClick) },
                    onShowTerms = { title, content ->
                        termsTitle = title
                        termsContent = content
                        showTermsSheet = true
                    }
                )
            } else {
                println("ProfileScreen - Showing GuestContent")
                GuestContent(
                    onLoginClick = { viewModel.onAction(ProfileAction.OnLoginClick) },
                    onRegisterClick = { viewModel.onAction(ProfileAction.OnRegisterClick) }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Version text
            Text(
                text = "V.${state.versionName}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        // Terms/Privacy sheet
        if (showTermsSheet) {
            TermsSheet(
                title = termsTitle,
                content = termsContent,
                onDismiss = { showTermsSheet = false }
            )
        }

        // Loading indicator
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YellowMain
            )
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProfileHeader(
    isSubscribed: Boolean,
    onGetProClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = RushonGroundFamily
        )

        if (!isSubscribed) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onGetProClick)
            ) {
                Image(
                    painter = painterResource(Res.drawable.crown_pro),
                    contentDescription = "Get Pro",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Get pro",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = NunitoBlackFamily
                )
            }
        }
    }
}

@Composable
fun LoggedInContent(
    state: ProfileState,
    onAccountDetailsClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onShareAppClick: () -> Unit,
    onRateAppClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onShowTerms: (String, String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture with crown
        Box(
            modifier = Modifier.size(100.dp)
        ) {
            AsyncImage(
                model = state.avatarUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.head_placeholder)
            )

            if (state.isSubscribed) {
                Image(
                    painter = painterResource(Res.drawable.crown_pro),
                    contentDescription = "Premium",
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-7).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Username
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    color = YellowMain
                )
            } else {
                Text(
                    text = state.username ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = NunitoMediumFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Menu items
        ProfileMenuItem(
            icon = Res.drawable.user_circle,
            text = "Account details",
            onClick = onAccountDetailsClick
        )

        ProfileMenuItem(
            icon = Res.drawable.contact,
            text = "Contact Us (gmail)",
            onClick = onContactUsClick
        )

        ProfileMenuItem(
            icon = Res.drawable.terms_of_use,
            text = "Terms of use",
            onClick = { onShowTerms("Terms of Service", getTermsContent()) }
        )

        ProfileMenuItem(
            icon = Res.drawable.shield,
            text = "Privacy policy",
            onClick = { onShowTerms("Privacy Policy", getPrivacyContent()) }
        )

        ProfileMenuItem(
            icon = Res.drawable.share_app,
            text = "Share app",
            onClick = onShareAppClick
        )

        ProfileMenuItem(
            icon = Res.drawable.rate_app,
            text = "Rate app",
            onClick = onRateAppClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        ProfileMenuItem(
            icon = Res.drawable.log_out_image,
            text = "Log out",
            onClick = onLogoutClick
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: Any,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 30.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon as org.jetbrains.compose.resources.DrawableResource),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            fontFamily = NunitoSemiBoldFamily
        )
        Image(
            painter = painterResource(Res.drawable.account_arrow),
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun GuestContent(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Guest mode active",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = YellowMain,
            fontFamily = RushonGroundFamily
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Sign up or login to unlock the full\nexperience!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = NunitoMediumFamily
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) {
            Text(
                text = "Login",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = RushonGroundFamily
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, YellowMain)
        ) {
            Text(
                text = "Create Account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = YellowMain,
                fontFamily = RushonGroundFamily
            )
        }
    }
}

@Composable
fun TermsSheet(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(DarkGrayCard)
                .padding(16.dp)
                .clickable(enabled = false) { }
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp),
                fontFamily = NunitoBoldFamily
            )

            Text(
                text = content,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                fontFamily = NunitoMediumFamily
            )
        }
    }
}

fun getTermsContent(): String {
    return """
1. Introduction
Welcome to ForeSport. By using our app, you agree to these Terms of Service.

2. User Information
We collect and store user information, including names, emails, and passwords. By using our app, you consent to this data collection.

3. Predictions Disclaimer
We provide predictions for SportyBet, 1xBet, Bet9ja, and BetKing. However, we are not responsible for any bets you place outside our app using these predictions.

4. Subscriptions & Ads
Our app includes subscriptions and advertisements to support our services.

5. No Refunds
All purchases and subscriptions are non-refundable. Please review your choices before making a payment.

6. Non-Transferable Subscriptions
Subscriptions are device-specific and cannot be transferred to another device. Even if an account is premium, it will only remain premium on the original device where the subscription was made.

7. Notifications
We may send notifications related to your account, updates, and promotions.

8. Data Storage
Your information is securely stored in our database.

9. Changes to Terms
We reserve the right to update these Terms at any time. Continued use of our app constitutes acceptance of the updated Terms.

10. Contact Us
If you have any questions about these Terms, please contact us.
    """.trimIndent()
}

fun getPrivacyContent(): String {
    return """
1. Introduction
Welcome to ForeSport. This Privacy Policy explains how we collect, use, and protect your personal information.

2. Information We Collect
We collect and store the following user data: names, emails, and passwords. By using our app, you consent to this data collection.

3. Use of Information
Your information is used for account management, security, and personalized experiences within the app.

4. Predictions Disclaimer
We provide predictions for SportyBet, 1xBet, Bet9ja, and BetKing. However, we are not responsible for any bets you place outside our app using these predictions.

5. Subscriptions & Ads
Our app includes subscriptions and advertisements to support our services.

6. No Refunds
All purchases and subscriptions are non-refundable. Please review your choices before making a payment.

7. Non-Transferable Subscriptions
Subscriptions are device-specific and cannot be transferred to another device.

8. Notifications
We may send notifications related to your account, updates, and promotions.

9. Data Storage
Your personal information is securely stored in our database and protected from unauthorized access.

10. Changes to This Policy
We reserve the right to update this Privacy Policy at any time. Continued use of our app constitutes acceptance of the updated policy.

11. Contact Us
If you have any questions regarding this Privacy Policy, please contact us.
    """.trimIndent()
}

val DarkGrayBackground = Color(0xFF1A1A2E)
val DarkGrayCard = Color(0xFF16213E)
val YellowMain = Color(0xFFFBBF24)
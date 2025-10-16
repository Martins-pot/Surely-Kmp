package com.sportmaster.surelykmp.activities.profile.presentation.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToAccountDetails: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        // Background texture
        Image(
            painter = painterResource("drawable/maintexture.png"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            ProfileHeader(
                onGetProClick = {
                    if (uiState.isLoggedIn) {
                        onNavigateToSubscription()
                    } else {
                        viewModel.showSnackbar("Log in to subscribe")
                    }
                },
                showProButton = !uiState.isSubscribed
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Content based on login state
            if (uiState.isLoggedIn) {
                LoggedInContent(
                    uiState = uiState,
                    onAccountDetailsClick = onNavigateToAccountDetails,
                    onContactClick = { viewModel.onContactUsClick() },
                    onTermsClick = { viewModel.toggleTermsView(true) },
                    onPrivacyClick = { viewModel.togglePrivacyView(true) },
                    onShareClick = { viewModel.onShareAppClick() },
                    onRateClick = { viewModel.onRateAppClick() },
                    onLogoutClick = { viewModel.logout() }
                )
            } else {
                GuestContent(
                    onLoginClick = onNavigateToLogin,
                    onRegisterClick = onNavigateToRegister
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Version text
            Text(
                text = uiState.versionText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        // Terms/Privacy bottom sheet
        BottomSheet(
            isVisible = uiState.showTermsSheet,
            content = uiState.termsContent,
            onDismiss = { viewModel.toggleTermsView(false) }
        )

        // Snackbar
        if (uiState.snackbarMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFF2A2A3E),
                contentColor = Color.White
            ) {
                Text(uiState.snackbarMessage!!)
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    onGetProClick: () -> Unit,
    showProButton: Boolean
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
            color = Color.White
        )

        if (showProButton) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = onGetProClick)
            ) {
                Icon(
                    painter = painterResource("drawable/crown_pro.png"),
                    contentDescription = "Get Pro",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFFFFD700)
                )
                Text(
                    text = "Get pro",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoggedInContent(
    uiState: ProfileUiState,
    onAccountDetailsClick: () -> Unit,
    onContactClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onShareClick: () -> Unit,
    onRateClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture with crown
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            AsyncImage(
                model = uiState.userAvatar,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
                placeholder = painterResource("drawable/head_placeholder.png"),
                error = painterResource("drawable/head_placeholder.png")
            )

            if (uiState.isSubscribed) {
                Icon(
                    painter = painterResource("drawable/crown_pro.png"),
                    contentDescription = "Premium",
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = 5.dp, y = (-7).dp)
                        .rotate(45f),
                    tint = Color(0xFFFFD700)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username
        if (uiState.isLoadingUser) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFFFF4444)
            )
        } else {
            Text(
                text = uiState.username ?: "User",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Menu items
        ProfileMenuItem(
            icon = "drawable/user_circle.png",
            text = "Account details",
            onClick = onAccountDetailsClick
        )

        ProfileMenuItem(
            icon = "drawable/contact.png",
            text = "Contact Us (gmail)",
            onClick = onContactClick
        )

        ProfileMenuItem(
            icon = "drawable/terms_of_use.png",
            text = "Terms of use",
            onClick = onTermsClick
        )

        ProfileMenuItem(
            icon = "drawable/shield.png",
            text = "Privacy policy",
            onClick = onPrivacyClick
        )

        ProfileMenuItem(
            icon = "drawable/share_app.png",
            text = "Share app",
            onClick = onShareClick
        )

        ProfileMenuItem(
            icon = "drawable/rate_app.png",
            text = "Rate app",
            onClick = onRateClick
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Logout button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogoutClick)
                .padding(horizontal = 30.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource("drawable/log_out_image.png"),
                contentDescription = "Logout",
                modifier = Modifier.size(24.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                painter = painterResource("drawable/logout.png"),
                contentDescription = "Logout Text",
                modifier = Modifier.height(20.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: String,
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
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource("drawable/account_arrow.png"),
            contentDescription = "Navigate",
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )
    }
}

@Composable
private fun GuestContent(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Guest mode active",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC107),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Sign up or login to unlock the full\nexperience!",
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(Color.White)
            )
        ) {
            Text(
                text = "Login",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFC107))
            )
        ) {
            Text(
                text = "Create Account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107)
            )
        }
    }
}

@Composable
private fun BottomSheet(
    isVisible: Boolean,
    content: String,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(expandFrom = Alignment.Bottom),
        exit = shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .background(
                    Color(0xFF2A2A3E),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .clickable(onClick = onDismiss)
        ) {
            val scrollState = rememberScrollState()

            Text(
                text = content,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
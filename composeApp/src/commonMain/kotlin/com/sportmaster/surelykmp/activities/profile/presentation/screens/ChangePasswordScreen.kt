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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordAction
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordEvent
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.ChangePasswordViewModel
import com.sportmaster.surelykmp.core.presentation.screens.Screen
import com.sportmaster.surelykmp.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ChangePasswordEvent.ShowError -> {
                    // Handle error
                    println("ChangePassword Error: ${event.message}")
                }
                is ChangePasswordEvent.ShowSuccess -> {
                    // Handle success
                    println("ChangePassword Success: ${event.message}")
                }
                ChangePasswordEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                ChangePasswordEvent.ShowImagePicker -> {
                    // Handle image picker
                    println("Show image picker")
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // Background
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (state.showSuccess) {
            SuccessView(
                onReturn = { navController.popBackStack() }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )

                    Text(
                        text = "Change Your Password",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = RushonGroundFamily
                    )

                    Spacer(modifier = Modifier.size(24.dp)) // For balance
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
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

                        Image(
                            painter = painterResource(Res.drawable.pfp_edit_pen),
                            contentDescription = "Edit Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .clickable { viewModel.onAction(ChangePasswordAction.PickImage) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Username
                    Text(
                        text = state.username ?: "Loading...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = NunitoMediumFamily
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // New Password
                    Text(
                        text = "New Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = NunitoMediumFamily,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PasswordTextField(
                        value = state.newPassword,
                        onValueChange = { viewModel.onAction(ChangePasswordAction.NewPasswordChanged(it)) },
                        isPasswordVisible = state.isNewPasswordVisible,
                        onToggleVisibility = { viewModel.onAction(ChangePasswordAction.ToggleNewPasswordVisibility) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.newPasswordError != null) {
                        Text(
                            text = state.newPasswordError!!,
                            fontSize = 10.sp,
                            color = Color.Red,
                            fontFamily = NunitoMediumFamily,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Repeat Password
                    Text(
                        text = "Repeat New Password",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = NunitoMediumFamily,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PasswordTextField(
                        value = state.repeatPassword,
                        onValueChange = { viewModel.onAction(ChangePasswordAction.RepeatPasswordChanged(it)) },
                        isPasswordVisible = state.isRepeatPasswordVisible,
                        onToggleVisibility = { viewModel.onAction(ChangePasswordAction.ToggleRepeatPasswordVisibility) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.repeatPasswordError != null) {
                        Text(
                            text = state.repeatPasswordError!!,
                            fontSize = 10.sp,
                            color = Color.Red,
                            fontFamily = NunitoMediumFamily,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))

                    // Confirm Button
                    Button(
                        onClick = { viewModel.onAction(ChangePasswordAction.ConfirmChanges) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEE5A52),
                            disabledContainerColor = Color(0xFFEE5A52).copy(alpha = 0.5f)
                        ),
                        enabled = state.isConfirmEnabled
                    ) {
                        Text(
                            text = "Confirm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = RushonGroundFamily
                        )
                    }
                }
            }

            // Loading
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = YellowMain
                )
            }
        }
    }
}

@Composable
fun SuccessView(
    onReturn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.success),
            contentDescription = "Success",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Success!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = RushonGroundFamily
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Congratulations! Your password has been changed.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = NunitoMediumFamily
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = onReturn,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEE5A52)
            )
        ) {
            Text(
                text = "Return to Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = RushonGroundFamily
            )
        }
    }
}
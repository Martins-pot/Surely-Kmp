package com.sportmaster.surelykmp.activities.register.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sportmaster.surelykmp.activities.profile.presentation.screens.YellowMain
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ForgotPasswordAction
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ForgotPasswordEvent
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.ForgotPasswordViewModel
import com.sportmaster.surelykmp.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.*

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ForgotPasswordEvent.ShowError -> {
                    println("ForgotPassword Error: ${event.message}")
                }
                is ForgotPasswordEvent.ShowSuccess -> {
                    println("ForgotPassword Success: ${event.message}")
                }
                ForgotPasswordEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is ForgotPasswordEvent.NavigateToChangePassword -> {
                    // Navigate to change password with user data
                    navController.navigate("changepassword/${event.email}")
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

        when {
            state.showOtpScreen -> {
                OtpVerificationView(
                    email = state.email,
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
            else -> {
                EmailInputView(
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.loadingMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = NunitoBoldFamily,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    CircularProgressIndicator(
                        color = YellowMain,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmailInputView(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Title
        Text(
            text = "Forgot Password",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = RushonGroundFamily
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Please enter your email to reset the password",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = NunitoMediumFamily
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Email Label
        Text(
            text = "Email Address",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            fontFamily = NunitoMediumFamily
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    color = if (state.emailError != null)
                        Color(0xFFEE5A52).copy(alpha = 0.2f)
                    else
                        Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (state.emailError != null)
                        Color(0xFFEE5A52)
                    else
                        Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            BasicTextField(
                value = state.email,
                onValueChange = { viewModel.onAction(ForgotPasswordAction.EmailChanged(it)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = NunitoMediumFamily
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                }
            )
        }

        if (state.emailError != null) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = state.emailError!!,
                fontSize = 10.sp,
                color = Color(0xFFEE5A52),
                fontFamily = NunitoMediumFamily
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Change Password Button
        Button(
            onClick = { viewModel.onAction(ForgotPasswordAction.SendOtp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEE5A52),
                disabledContainerColor = Color(0xFFEE5A52).copy(alpha = 0.5f)
            ),
            enabled = state.isConfirmEnabled && !state.isLoading
        ) {
            Text(
                text = "Change Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = RushonGroundFamily
            )
        }
    }
}

@Composable
fun OtpVerificationView(
    email: String,
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel
) {
    val state by viewModel.state.collectAsState()
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Title
        Text(
            text = "Check Your Email",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = RushonGroundFamily
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "We sent a code to $email\nenter the code you received",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = NunitoMediumFamily
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            OtpTextField(
                value = otp1,
                onValueChange = { if (it.length <= 1) otp1 = it },
                modifier = Modifier.weight(1f)
            )
            OtpTextField(
                value = otp2,
                onValueChange = { if (it.length <= 1) otp2 = it },
                modifier = Modifier.weight(1f)
            )
            OtpTextField(
                value = otp3,
                onValueChange = { if (it.length <= 1) otp3 = it },
                modifier = Modifier.weight(1f)
            )
            OtpTextField(
                value = otp4,
                onValueChange = { if (it.length <= 1) otp4 = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Verify Button
        Button(
            onClick = {
                val otp = "$otp1$otp2$otp3$otp4"
                viewModel.onAction(ForgotPasswordAction.VerifyOtp(otp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEE5A52),
                disabledContainerColor = Color(0xFFEE5A52).copy(alpha = 0.5f)
            ),
            enabled = otp1.isNotEmpty() && otp2.isNotEmpty() &&
                    otp3.isNotEmpty() && otp4.isNotEmpty() && !state.isLoading
        ) {
            Text(
                text = "Verify Code",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = RushonGroundFamily
            )
        }

        Spacer(modifier = Modifier.height(130.dp))

        // Resend OTP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Didn't receive a code?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontFamily = NunitoMediumFamily
            )

            Text(
                text = if (state.resendCountdown > 0)
                    " Resend OTP (${state.resendCountdown}s)"
                else
                    " Resend OTP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.resendCountdown > 0)
                    Color.Gray
                else
                    Color(0xFF2196F3),
                fontFamily = NunitoMediumFamily,
                modifier = Modifier.clickable(enabled = state.resendCountdown == 0) {
                    viewModel.onAction(ForgotPasswordAction.ResendOtp)
                }
            )
        }
    }
}

@Composable
fun OtpTextFieldForgot(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .background(
                color = if (value.isNotEmpty())
                    Color(0xFF4CAF50).copy(alpha = 0.2f)
                else
                    Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = if (value.isNotEmpty())
                    Color(0xFF4CAF50)
                else
                    Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= 1 && it.all { char -> char.isDigit() })
                    onValueChange(it)
            },
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontFamily = NunitoBoldFamily
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}
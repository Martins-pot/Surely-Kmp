package com.sportmaster.surelykmp.activities.register.presentation.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.sportmaster.surelykmp.core.data.model.AuthState
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.*
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil3.Uri
import com.mertswork.footyreserve.ui.theme.Dimens
import com.sportmaster.surelykmp.activities.register.presentation.viewmodels.RegisterViewModel
import com.sportmaster.surelykmp.core.presentation.screens.Screen

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToLogin: (() -> Unit)? = null,
    onNavigateToRegister: (() -> Unit)? = null,
    navController: NavController
) {
    var showOtpScreen by remember { mutableStateOf(false) }
    var registeredEmail by remember { mutableStateOf("") }
    var registeredUsername by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val isLoginMode by viewModel.isLoginMode.collectAsState()

    // Track current destination to prevent unwanted navigation
    var currentDestination by remember { mutableStateOf("login") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.OtpSent -> {
                showOtpScreen = true
                currentDestination = "otp"
            }
            is AuthState.Success -> {
                // Only navigate to profile if we're on login screen and login was successful
                if (currentDestination == "login" && isLoginMode) {
                    onLoginSuccess()
                    viewModel.resetState()
                }
                // For OTP verification success, just close OTP screen
                else if (currentDestination == "otp") {
                    showOtpScreen = false
                    currentDestination = "register"
                    viewModel.resetState()
                }
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(Res.drawable.background_texture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when {
            showOtpScreen -> OtpVerificationScreen(
                email = registeredEmail,
                username = registeredUsername,
                viewModel = viewModel,
                onBack = {
                    showOtpScreen = false
                    currentDestination = "register"
                    viewModel.resetState()
                },
                onVerified = {
                    showOtpScreen = false
                    currentDestination = "register"
                    viewModel.resetState()
                }
            )
            isLoginMode -> {
                // Update current destination when showing login form
                LaunchedEffect(Unit) {
                    currentDestination = "login"
                }
                LoginForm(
                    viewModel = viewModel,
                    onNavigateBack = onNavigateBack,
                    onToggleMode = {
                        if (onNavigateToRegister != null) {
                            onNavigateToRegister()
                        } else {
                            viewModel.toggleMode()
                        }
                    },
                    navController = navController
                )
            }
            else -> {
                // Update current destination when showing register form
                LaunchedEffect(Unit) {
                    currentDestination = "register"
                }
                RegisterForm(
                    viewModel = viewModel,
                    onNavigateBack = onNavigateBack,
                    onToggleMode = {
                        if (onNavigateToLogin != null) {
                            onNavigateToLogin()
                        } else {
                            viewModel.toggleMode()
                        }
                    },
                    onOtpSent = { email, username ->
                        registeredEmail = email
                        registeredUsername = username
                        currentDestination = "otp"
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterForm(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onToggleMode: () -> Unit,
    onOtpSent: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordFields by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val usernameError by viewModel.usernameError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(30.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Image(
                painter = painterResource(Res.drawable.logo_tiny),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Join ForeSport Today!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Create an account to access betting codes effortlessly.",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Profile Image Picker
        ProfileImagePicker(
            imageUri = selectedImageUri,
            onImageSelected = { uri -> selectedImageUri = uri }
        )

        Spacer(modifier = Modifier.height(25.dp))

        if (!showPasswordFields) {
            // Email and Username Fields
            CustomTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.checkEmailAvailability(it)
                },
                label = "Email *",
                placeholder = "Enter your email",
                errorMessage = emailError,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                value = username,
                onValueChange = {
                    val filtered = it.filter { char -> char.isLetterOrDigit() || char == '_' }
                    if (filtered.length <= 15) {
                        username = filtered
                        viewModel.checkUsernameAvailability(filtered)
                    }
                },
                label = "Username *",
                placeholder = "Choose a unique username",
                errorMessage = usernameError
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Next Button
            Button(
                onClick = {
                    if (emailError == null && usernameError == null &&
                        email.isNotEmpty() && username.isNotEmpty() && selectedImageUri != null) {
                        showPasswordFields = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = emailError == null && usernameError == null &&
                        email.isNotEmpty() && username.isNotEmpty() && selectedImageUri != null
            ) {
                Text("Next", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // Password Fields
            CustomTextField(
                value = password,
                onValueChange = {
                    password = it.filter { char -> !char.isWhitespace() }
                    viewModel.validatePassword(password)
                },
                label = "Password *",
                placeholder = "Create a strong password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                errorMessage = passwordError,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it.filter { char -> !char.isWhitespace() }
                },
                label = "Confirm Password *",
                placeholder = "Repeat password",
                isPassword = true,
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                errorMessage = if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                    "Passwords do not match"
                } else null,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Register Button
            Button(
                onClick = {
                    if (selectedImageUri != null) {
                        viewModel.register(
                            username = username,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            imageUri = selectedImageUri!!
                        )
                        onOtpSent(email, username)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = authState !is AuthState.Loading && selectedImageUri != null
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Verify Email", fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Toggle to Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account?",
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = " Login",
                fontSize = 12.sp,
                color = Color(0xFF2196F3),
                modifier = Modifier.clickable { onToggleMode() },
                fontWeight = FontWeight.Bold
            )
        }

        // Error/Success Messages
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LoginForm(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onToggleMode: () -> Unit,
    navController: NavController

) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val isEmailLoginMode by viewModel.isEmailLoginMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(30.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Image(
                painter = painterResource(Res.drawable.logo_tiny),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Welcome back!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Log in to discover today's winning codes.",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Login Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LoginToggleButton(
                text = "Email Address",
                isSelected = isEmailLoginMode,
                onClick = { viewModel.toggleLoginMethod(true) },
                modifier = Modifier.weight(1f)
            )
            LoginToggleButton(
                text = "Username",
                isSelected = !isEmailLoginMode,
                onClick = { viewModel.toggleLoginMethod(false) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Username/Email Field
        CustomTextField(
            value = usernameOrEmail,
            onValueChange = { usernameOrEmail = it },
            label = if (isEmailLoginMode) "Email" else "Username",
            placeholder = "",
            keyboardType = if (isEmailLoginMode) KeyboardType.Email else KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Field
        Text(
            text = "Password",
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "",
            placeholder = "",
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Remember Me & Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE53935),
                        uncheckedColor = Color.White
                    )
                )
                Text(
                    text = "Remember me",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }

            Text(
                text = "Forgotten password?",
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier.clickable {
                    viewModel.resetState()
                    navController.navigate(Screen.ChangePassword.route) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Login Button
        Button(
            onClick = {
                viewModel.login(usernameOrEmail, password, isEmailLoginMode)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            ),
            shape = RoundedCornerShape(10.dp),
            enabled = authState !is AuthState.Loading && usernameOrEmail.isNotEmpty() && password.isNotEmpty()
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Login", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Guest Button
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, Color(0xFFFFC107)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "Explore as Guest",
                fontSize = 14.sp,
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Toggle to Register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account?",
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = " Create Account",
                fontSize = 12.sp,
                color = Color(0xFF2196F3),
                modifier = Modifier.clickable { onToggleMode() },
                fontWeight = FontWeight.Bold
            )
        }

        // Error Messages
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OtpVerificationScreen(
    email: String,
    username: String,
    viewModel: RegisterViewModel,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onVerified()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Image(
                painter = painterResource(Res.drawable.logo_tiny),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Title
        Text(
            text = "Check Your Email",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "We sent a reset link to $email\nenter 4 digit code that mentioned in the email",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            OtpTextField(value = otp1, onValueChange = { if (it.length <= 1) otp1 = it }, modifier = Modifier.weight(1f))
            OtpTextField(value = otp2, onValueChange = { if (it.length <= 1) otp2 = it }, modifier = Modifier.weight(1f))
            OtpTextField(value = otp3, onValueChange = { if (it.length <= 1) otp3 = it }, modifier = Modifier.weight(1f))
            OtpTextField(value = otp4, onValueChange = { if (it.length <= 1) otp4 = it }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Verify Button
        Button(
            onClick = {
                val otp = "$otp1$otp2$otp3$otp4"
                viewModel.verifyOtp(email, otp, onVerified)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            ),
            shape = RoundedCornerShape(10.dp),
            enabled = authState !is AuthState.Loading && otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty()
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Verify Code", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
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
                color = Color.White
            )
            Text(
                text = " Resend OTP",
                fontSize = 12.sp,
                color = Color(0xFF2196F3),
                modifier = Modifier.clickable {
                    viewModel.resendOtp(email, username)
                },
                fontWeight = FontWeight.Bold
            )
        }

        // Error Messages
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(
                    color = if (errorMessage != null) Color(0xFFE53935).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (errorMessage != null) Color(0xFFE53935) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                if (isPassword) {
                    IconButton(
                        onClick = onPasswordVisibilityToggle,
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(if (passwordVisible) Res.drawable.eye_open else Res.drawable.eye_closed),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFE53935),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= 1 && it.all { char -> char.isDigit() }) onValueChange(it) },
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LoginToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(45.dp)
            .background(
                color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
expect fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
)
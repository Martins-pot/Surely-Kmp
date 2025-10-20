package com.sportmaster.surelykmp.activities.profile.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsAction
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsEvent
import com.sportmaster.surelykmp.activities.profile.presentation.viewmodels.AccountDetailsViewModel
import com.sportmaster.surelykmp.activities.register.presentation.screens.ProfileImagePicker
import com.sportmaster.surelykmp.core.presentation.screens.Screen
import com.sportmaster.surelykmp.ui.theme.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import surelykmp.composeapp.generated.resources.*
import androidx.compose.runtime.collectAsState

@Composable
fun AccountDetailsScreen(
    navController: NavController,
    viewModel: AccountDetailsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // State for image picker
    var showImagePicker by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AccountDetailsEvent.ShowError -> {
                    // Handle error (you can show snackbar here)
                    println("AccountDetails Error: ${event.message}")
                }
                is AccountDetailsEvent.ShowSuccess -> {
                    // Handle success
                    println("AccountDetails Success: ${event.message}")
                }
                AccountDetailsEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                AccountDetailsEvent.ShowImagePicker -> {
                    showImagePicker = true
                }
            }
        }
    }

    // Handle image selection
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            // Convert URI to ByteArray and update profile
            // You'll need to implement this in your ViewModel
            // For now, we'll just update the local state
            println("Image selected: $uri")
            // viewModel.onImageSelected(uri) // You'll need to add this method to your ViewModel
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                    text = "ACCOUNT DETAILS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = RushonGroundFamily
                )

                Spacer(modifier = Modifier.size(24.dp)) // For balance
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture with ProfileImagePicker
                Box(
                    modifier = Modifier.size(120.dp)
                ) {
                    // Use ProfileImagePicker component
                    ProfileImagePicker(
                        imageUri = state.avatarUrl ?: selectedImageUri,
                        onImageSelected = { uri ->
                            selectedImageUri = uri
                            // Handle the image selection in ViewModel
                            // You'll need to add this functionality to your ViewModel
                        },
////                        modifier = Modifier
//                            .size(100.dp)
//                            .align(Alignment.Center)
//                            .clip(CircleShape)
                    )

                    // Edit icon overlay
                    Image(
                        painter = painterResource(Res.drawable.pfp_edit_pen),
                        contentDescription = "Edit Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.BottomEnd)
                            .clickable {
                                viewModel.onAction(AccountDetailsAction.PickImage)
                                showImagePicker = true
                            }
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

                // Current Password
                Text(
                    text = "Current Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = NunitoMediumFamily,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(10.dp))

                PasswordTextField(
                    value = state.currentPassword,
                    onValueChange = { viewModel.onAction(AccountDetailsAction.CurrentPasswordChanged(it)) },
                    isPasswordVisible = state.isCurrentPasswordVisible,
                    onToggleVisibility = { viewModel.onAction(AccountDetailsAction.ToggleCurrentPasswordVisibility) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.currentPasswordError != null) {
                    Text(
                        text = state.currentPasswordError!!,
                        fontSize = 10.sp,
                        color = Color.Red,
                        fontFamily = NunitoMediumFamily,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 5.dp)
                    )
                }

                // New Password Section (only shown when current password is valid)
                if (state.showNewPasswordFields) {
                    Spacer(modifier = Modifier.height(20.dp))

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
                        onValueChange = { viewModel.onAction(AccountDetailsAction.NewPasswordChanged(it)) },
                        isPasswordVisible = state.isNewPasswordVisible,
                        onToggleVisibility = { viewModel.onAction(AccountDetailsAction.ToggleNewPasswordVisibility) },
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
                        onValueChange = { viewModel.onAction(AccountDetailsAction.RepeatPasswordChanged(it)) },
                        isPasswordVisible = state.isRepeatPasswordVisible,
                        onToggleVisibility = { viewModel.onAction(AccountDetailsAction.ToggleRepeatPasswordVisibility) },
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
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Confirm Button
                Button(
                    onClick = { viewModel.onAction(AccountDetailsAction.ConfirmChanges) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f)
                    ),
                    enabled = state.isConfirmEnabled
                ) {
                    Text(
                        text = "Confirm",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = RushonGroundFamily
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delete Account Button
                Button(
                    onClick = { viewModel.onAction(AccountDetailsAction.ShowDeleteDialog) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF6B6B), Color(0xFFEE5A52))
                        )
                    )
                ) {
                    Text(
                        text = "Delete Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B),
                        fontFamily = RushonGroundFamily
                    )
                }
            }
        }

        // Delete Confirmation Dialog
        if (state.showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = { viewModel.onAction(AccountDetailsAction.DeleteAccount) },
                onCancel = { viewModel.onAction(AccountDetailsAction.HideDeleteDialog) }
            )
        }

        // Profile Image Picker Dialog
        if (showImagePicker) {
            ProfileImagePickerDialog(
                onImageSelected = { uri ->
                    selectedImageUri = uri
                    showImagePicker = false
                    // Handle image update in ViewModel
                    // You'll need to implement this
                },
                onDismiss = { showImagePicker = false }
            )
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

// Custom ProfileImagePickerDialog that uses the same ProfileImagePicker component
@Composable
fun ProfileImagePickerDialog(
    onImageSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempImageUri by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkGrayCard)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Change Profile Picture",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = RushonGroundFamily,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Use the same ProfileImagePicker component
                ProfileImagePicker(
                    imageUri = tempImageUri,
                    onImageSelected = { uri -> tempImageUri = uri },
//                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RushonGroundFamily
                        )
                    }

                    // Confirm Button
                    Button(
                        onClick = {
                            onImageSelected(tempImageUri)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        enabled = tempImageUri != null
                    ) {
                        Text(
                            text = "Confirm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = RushonGroundFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visualTransformation = if (isPasswordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(50.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor =  Color.White.copy(alpha = 0.5f),
            cursorColor = Color.White,
            focusedLabelColor =  Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.5f)
        ),

        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    painter = painterResource(
                        if (isPasswordVisible) Res.drawable.eye_open else Res.drawable.eye_closed
                    ),
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkGrayCard)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.delete_trash),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Delete your account?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = RushonGroundFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "This action is permanent and cannot be undone. All your data will be lost, and you won't be able to recover your account.",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = NunitoSemiBoldFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RushonGroundFamily
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF6B6B), Color(0xFFEE5A52))
                        )
                    )
                ) {
                    Text(
                        text = "Delete Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = RushonGroundFamily
                    )
                }
            }
        }
    }
}
package com.sportmaster.surelykmp.activities.comment.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mertswork.footyreserve.ui.theme.DarkGrayBackground
import com.sportmaster.surelykmp.activities.comment.presentation.viewmodels.CommentViewModel
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.data.model.Comment
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.background_texture

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    viewModel: CommentViewModel,
    isLoggedIn: Boolean,
    isSubscribed: Boolean,
    currentUser: String,
    currentUserAvatar: String?,
    onBackClick: () -> Unit,
    onCopyCode: (String) -> Unit,
    onShareCode: (String) -> Unit,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show error/success messages
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    val code = uiState.code ?: return
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = code.text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        if (!isSubscribed) {
                            IconButton(onClick = {
                                if (isLoggedIn) {
                                    onSubscribeClick()
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Log in to subscribe")
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Get Pro",
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1A1A2E)
                    )
                )
            }
        ) { padding ->
            Box(modifier = modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        CodeDetailsCard(
                            code = code,
                            selectedRating = uiState.selectedRating,
                            isLoggedIn = isLoggedIn,
                            isRatingLoading = uiState.isRating,
                            onRatingSelected = { viewModel.onRatingSelected(it) },
                            onRateClick = { viewModel.rateCode() },
                            onCopyClick = { onCopyCode(code.text) },
                            onShareClick = { onShareCode(code.text) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${code.comments.size}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                    }

                    items(code.comments) { comment ->
                        CommentItem(comment = comment)
                    }
                }

                // Comment input at bottom
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(120.dp),
                    color = Color.Black
                ) {
                    if (isLoggedIn) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = uiState.commentText,
                                onValueChange = { viewModel.onCommentTextChanged(it) },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Write a comment") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(30.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (uiState.commentText.isNotBlank()) {
                                            viewModel.postComment(currentUser, currentUserAvatar)
                                        }
                                    }
                                ),
                                maxLines = 1,
                                enabled = !uiState.isCommentPosting
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            if (uiState.isCommentPosting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF4CAF50)
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        if (uiState.commentText.isNotBlank()) {
                                            viewModel.postComment(currentUser, currentUserAvatar)
                                        }
                                    },
                                    enabled = uiState.commentText.isNotBlank()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = if (uiState.commentText.isNotBlank())
                                            Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 30.dp)
                                .height(50.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Log in to comment", color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Loading overlay
                if (uiState.isRating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.Red)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Rating, please wait", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeDetailsCard(
    code: Code,
    selectedRating: Double,
    isLoggedIn: Boolean,
    isRatingLoading: Boolean,
    onRatingSelected: (Double) -> Unit,
    onRateClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            // Ratings and Views Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ratings", color = Color.Gray, fontSize = 10.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text =  "${code.rating/10 * 10 }",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = when {
                                (code.odds ?: 0.0) <= 10 -> "1k+"
                                (code.odds ?: 0.0) in 11.0..30.76 -> "500+"
                                else -> "100+"
                            },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Text("Code Users", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Copy and Share Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCopyClick() }
                ) {
                    Text("Copy Code", color = Color.Gray, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onShareClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Share Code", color = Color.Gray, fontSize = 10.sp)
                }
            }

            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(25.dp))

                // Rating Section
                Text("Rate this code", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onRatingSelected(i.toDouble()) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onRateClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    enabled = !isRatingLoading && selectedRating > 0
                ) {
                    if (isRatingLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Rate", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = comment.image ?: "",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = comment.user ?: "Unknown",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = comment.comment ?: "",
            color = Color.White,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = comment.createdAt ?: "Just now",
            color = Color.Gray,
            fontSize = 12.sp
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color.Gray.copy(alpha = 0.3f)
        )
    }
}
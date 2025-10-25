package com.sportmaster.surelykmp.activities.premiumcodes.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import kotlinx.coroutines.delay
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.CodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.ShimmerCodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.ui.theme.RushonGroundFamily
import com.sportmaster.surelykmp.utils.rememberShareManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.background_texture

@Composable
fun CodesScreenPremium(
    viewModel: PremiumCodesViewModel,
    onCodeClick: (Code) -> Unit = {}
) {
    val shareManager = rememberShareManager()
    val premiumState by viewModel.premiumState.collectAsState()
    var clickEnabled by remember { mutableStateOf(true) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Animation states
    val blurRadius = remember { Animatable(0f) }
    val dialogScale = remember { Animatable(0.8f) }
    val dialogAlpha = remember { Animatable(0f) }

    LaunchedEffect(clickEnabled) {
        if (!clickEnabled) {
            delay(4000L)
            clickEnabled = true
        }
    }

    // Calculate if we should show blur and dialog
    val shouldShowBlur = premiumState.isBlurActive &&
            !premiumState.isSubscribed &&
            !premiumState.isTimerActive &&
            !viewModel.isLoading &&
            viewModel.error == null &&
            viewModel.codes.isNotEmpty()

    // Animate blur and dialog simultaneously when conditions change
    LaunchedEffect(shouldShowBlur) {
        if (shouldShowBlur) {
            launch {
                blurRadius.animateTo(
                    targetValue = 6f,
                    animationSpec = tween(durationMillis = 1700)
                )
            }

            launch {
                dialogScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                dialogAlpha.animateTo(1f, animationSpec = tween(durationMillis = 500))
            }
        } else {
            launch {
                dialogAlpha.animateTo(0f, animationSpec = tween(durationMillis = 200))
                dialogScale.animateTo(0.8f, animationSpec = tween(durationMillis = 300))
            }

            launch {
                blurRadius.animateTo(0f, animationSpec = tween(durationMillis = 800))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            // Header with filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXPERT CODES",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RushonGroundFamily,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Country Filter Button
                    IconButton(
                        onClick = { showCountryDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        CountryIcon(country = viewModel.selectedCountry)
                    }

                    // Filter Button
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Sport Tabs
            SportTabSelector(
                selectedSport = viewModel.selectedSport,
                onSportSelected = { viewModel.selectSport(it) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Timer display
            if (premiumState.isTimerActive) {
                CountdownTimer(
                    timeRemaining = premiumState.timeRemaining,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                )
            }

            // Content area - THIS gets blurred
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .blur(radius = blurRadius.value.dp)
            ) {
                when {
                    viewModel.isLoading -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                        ) {
                            items(7) {
                                ShimmerCodeItem()
                            }
                            item {
                                Spacer(modifier = Modifier.height(60.dp))
                            }
                        }
                    }
                    viewModel.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = viewModel.error!!,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.retry() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE53935)
                                    )
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    viewModel.codes.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(70.dp))
                                Text(
                                    text = "No premium codes available for ${viewModel.selectedSport.displayName}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (blurRadius.value > 0f) {
                                Image(
                                    painter = painterResource(Res.drawable.background_texture),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(blurRadius.value / 5f),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(viewModel.codes) { code ->
                                    CodeItem(
                                        code = code,
                                        onShare = { text ->
                                            shareManager.shareText(text, "Share Code")
                                        },
                                        onItemClick = {
                                            if (clickEnabled) {
                                                clickEnabled = false
                                                onCodeClick(code)
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Premium Dialog
        if (dialogAlpha.value > 0f || blurRadius.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(dialogScale.value)
                        .alpha(dialogAlpha.value)
                ) {
                    Card(
                        modifier = Modifier.padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        PremiumDialogContent(
                            onWatchAd = { viewModel.onWatchAdClicked() },
                            onUpgrade = { /* navigate to upgrade */ }
                        )
                    }
                }
            }
        }

        // Country Selection Bottom Sheet
        if (showCountryDialog) {
            BottomSheetDialog(
                onDismiss = { showCountryDialog = false }
            ) {
                CountrySelectionContent(
                    selectedCountry = viewModel.selectedCountry,
                    onCountrySelected = { country ->
                        viewModel.selectCountry(country)
                        showCountryDialog = false
                    },
                    onDismiss = { showCountryDialog = false }
                )
            }
        }

        // Filter Selection Bottom Sheet
        if (showFilterDialog) {
            BottomSheetDialog(
                onDismiss = { showFilterDialog = false }
            ) {
                FilterSelectionContent(
                    selectedFilter = viewModel.selectedFilter,
                    onFilterSelected = { filter ->
                        viewModel.selectFilter(filter)
                        showFilterDialog = false
                    },
                    onDismiss = { showFilterDialog = false }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetDialog(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val slideOffset = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        slideOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = (slideOffset.value * 1000).dp)
                    .clickable(enabled = false) { }
            ) {
                content()
            }
        }
    }
}

@Composable
private fun CountryIcon(country: String) {
    Text(
        text = when (country) {
            "Nigeria" -> "🇳🇬"
            "Ghana" -> "🇬🇭"
            "Kenya" -> "🇰🇪"
            "Uganda" -> "🇺🇬"
            "Tanzania" -> "🇹🇿"
            "Cameroon" -> "🇨🇲"
            "South Africa" -> "🇿🇦"
            else -> "🌍"
        },
        fontSize = 20.sp
    )
}

@Composable
private fun CountrySelectionContent(
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val countries = listOf(
        "default" to "All Countries (Default)",
        "Nigeria" to "Nigeria",
        "Ghana" to "Ghana",
        "Kenya" to "Kenya",
        "Uganda" to "Uganda",
        "Tanzania" to "Tanzania",
        "Cameroon" to "Cameroon",
        "South Africa" to "South Africa"
    )

    var tempSelectedCountry by remember { mutableStateOf(selectedCountry) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Top curve indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Country",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RushonGroundFamily,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Subtitle
            Text(
                text = "Choose your country to personalize your experience.",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Country List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(countries) { (key, label) ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (key != "default") {
                                Text(
                                    text = when (key) {
                                        "Nigeria" -> "🇳🇬"
                                        "Ghana" -> "🇬🇭"
                                        "Kenya" -> "🇰🇪"
                                        "Uganda" -> "🇺🇬"
                                        "Tanzania" -> "🇹🇿"
                                        "Cameroon" -> "🇨🇲"
                                        "South Africa" -> "🇿🇦"
                                        else -> "🌍"
                                    },
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            } else {
                                Text(
                                    text = "🌍",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }

                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            RadioButton(
                                selected = tempSelectedCountry == key,
                                onClick = {
                                    tempSelectedCountry = key
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE53935),
                                    unselectedColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (key != countries.last().first) {
                            Divider(
                                color = Color.White.copy(alpha = 0.1f),
                                thickness = 0.7.dp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Select Button - Increased to 55dp
            Button(
                onClick = {
                    onCountrySelected(tempSelectedCountry)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp) // Increased to 55dp
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Select Country",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RushonGroundFamily
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

        }
    }
}

@Composable
private fun FilterSelectionContent(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val filters = listOf(
        "default" to "All Predictions (Default)",
        "high odds" to "High Odds (30+ odds)",
        "low risk" to "Low Risk (2-10 odds)",
        "sure odds" to "Sure Odds (Accuracy: 70% or more)"
    )

    var tempSelectedFilter by remember { mutableStateOf(selectedFilter) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Top curve indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Your Picks",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RushonGroundFamily,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Subtitle
            Text(
                text = "From sure wins to high-stakes odds – find predictions for you.",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(filters) { (key, label) ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            RadioButton(
                                selected = tempSelectedFilter == key,
                                onClick = {
                                    tempSelectedFilter = key
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFE53935),
                                    unselectedColor = Color.White.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (key != filters.last().first) {
                            Divider(
                                color = Color.White.copy(alpha = 0.1f),
                                thickness = 0.7.dp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Select Button - Increased to 55dp
            Button(
                onClick = {
                    onFilterSelected(tempSelectedFilter)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp) // Increased to 55dp
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Filter Results",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RushonGroundFamily
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PremiumDialogContent(
    onWatchAd: () -> Unit,
    onUpgrade: () -> Unit
) {
    val internalScale = remember { Animatable(0.8f) }
    val internalAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        internalScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        internalAlpha.animateTo(1f, animationSpec = tween(durationMillis = 400))
    }

    Box(
        modifier = Modifier
            .padding(25.dp)
            .wrapContentSize()
            .border(
                width = .5.dp,
                color = Color.White.copy(.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .scale(internalScale.value)
            .alpha(internalAlpha.value)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Red.copy(.15f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unlock",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unlock",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE53935)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PRO Codes",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Expert codes are locked for free users",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onWatchAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(.18f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Ad",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Watch ads to unlock for 20 minutes",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CountdownTimer(
    timeRemaining: Long,
    modifier: Modifier = Modifier
) {
    val minutes = (timeRemaining / 1000) / 60
    val seconds = (timeRemaining / 1000) % 60

    val textColor = when {
        minutes >= 6 -> Color.White
        minutes in 3..5 -> Color(0xFFFF9500)
        else -> Color(0xFFD52127)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Time remaining: ",
                color = Color.White,
                fontSize = 14.sp
            )
            val formatted = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            Text(
                text = formatted,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
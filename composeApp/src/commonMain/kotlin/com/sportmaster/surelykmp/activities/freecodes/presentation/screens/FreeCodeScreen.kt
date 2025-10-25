package com.sportmaster.surelykmp.activities.freecodes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.CodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.ShimmerCodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.ui.theme.RushonGroundFamily
import com.sportmaster.surelykmp.utils.rememberShareManager
import kotlinx.coroutines.delay
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
import androidx.compose.material.icons.filled.*

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mertswork.footyreserve.ui.theme.AppRed


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesScreen(
    viewModel: CodesViewModel,
    onCodeClick: (Code) -> Unit = {},
) {
    val shareManager = rememberShareManager()
    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    var clickEnabled by remember { mutableStateOf(true) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(clickEnabled) {
        if (!clickEnabled) {
            delay(4000L)
            clickEnabled = true
        }
    }
    // Handle refresh completion
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshCodes()
            delay(500) // Small delay for smooth animation
            isRefreshing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content in a Column with weight
            Column(
                modifier = Modifier
                    .weight(1f)
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
                        text = "CODES",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontFamily = RushonGroundFamily,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
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
                                imageVector = Icons.Default.FilterList,
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
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Content with pull-to-refresh
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { isRefreshing = true },
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Content
                    when {
                        viewModel.isLoading -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                            ) {
                                items(5) {
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
                                        fontSize = 16.sp
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
                                Text(
                                    text = "No codes available for ${viewModel.selectedSport.displayName}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        else -> {
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
                                        }
                                    )
                                }

                                // Add some bottom padding to ensure content doesn't get cut off by banner
                                item {
                                    Spacer(modifier = Modifier.height(0.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Banner Ad at the bottom
//        UnityBannerAd(
//            placementId = "Banner_Android",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp)
//        )
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

            Spacer(modifier = Modifier.height(20.dp))

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

            Button(
                onClick ={
                    onCountrySelected(tempSelectedCountry)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppRed.copy(.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppRed)
            ) {
                Text(
                    text = "Select Country",
                    fontSize = 14.sp,
                    color = Color.White,
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

            Spacer(modifier = Modifier.height(20.dp))

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
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick ={
                    onFilterSelected(tempSelectedFilter)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppRed.copy(.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppRed)
            ) {
                Text(
                    text = "Filter Results",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontFamily = RushonGroundFamily
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun UnityBannerAd(
    placementId: String,
    modifier: Modifier = Modifier
) {
    // Platform-specific banner ad implementation
    PlatformBannerAd(
        placementId = placementId,
        modifier = modifier
    )
}

// This will be implemented differently for each platform
@Composable
expect fun PlatformBannerAd(
    placementId: String,
    modifier: Modifier = Modifier
)

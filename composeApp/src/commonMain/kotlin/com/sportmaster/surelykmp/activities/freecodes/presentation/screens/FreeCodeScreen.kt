package com.sportmaster.surelykmp.activities.freecodes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.CodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodesScreen(
    viewModel: CodesViewModel
) {
    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }

    // Handle refresh completion
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.refreshCodes()
            delay(500) // Small delay for smooth animation
            isRefreshing = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content in a Column with weight
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    text = "CODES",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
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
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFE53935))
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
                            items(viewModel.codes.filter { !it.isExpensive }) { code ->
                                CodeItem(
                                    code = code,
                                    onShare = {},
                                    onItemClick = {}
                                )
                            }

                            // Add some bottom padding to ensure content doesn't get cut off by banner
                            item {
                                Spacer(modifier = Modifier.height(60.dp))
                            }
                        }
                    }
                }
            }
        }

        // Banner Ad at the bottom
        UnityBannerAd(
            placementId = "Banner_Android",
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )
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
//
//@Composable
//fun CodesScreen(
//    viewModel: CodesViewModel
//) {
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Main content in a Column with weight
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(vertical = 16.dp)
//        ) {
//            // Header
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 20.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    modifier = Modifier
//                        .padding(horizontal = 20.dp),
//                    text = "CODES",
//                    color = Color.White,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            // Sport Tabs
//            SportTabSelector(
//                selectedSport = viewModel.selectedSport,
//                onSportSelected = { viewModel.selectSport(it) },
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Content
//            when {
//                viewModel.isLoading -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(color = Color(0xFFE53935))
//                    }
//                }
//                viewModel.error != null -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                text = viewModel.error!!,
//                                color = Color.White,
//                                fontSize = 16.sp
//                            )
//                            Spacer(modifier = Modifier.height(16.dp))
//                            Button(
//                                onClick = { viewModel.retry() },
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = Color(0xFFE53935)
//                                )
//                            ) {
//                                Text("Retry")
//                            }
//                        }
//                    }
//                }
//                viewModel.codes.isEmpty() -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "No codes available for ${viewModel.selectedSport.displayName}",
//                            color = Color.White,
//                            fontSize = 16.sp
//                        )
//                    }
//                }
//                else -> {
//                    LazyColumn(
//                        verticalArrangement = Arrangement.spacedBy(12.dp),
////                        modifier = Modifier.padding(horizontal = 16.dp)
//                    ) {
//                        items(viewModel.codes.filter { !it.isExpensive }) { code ->
//                            CodeItem(
//                                code = code,
//                                onShare = {},
//                                onItemClick = {}
//                            )
//                        }
//
//                        // Add some bottom padding to ensure content doesn't get cut off by banner
//                        item {
//                            Spacer(modifier = Modifier.height(60.dp))
//                        }
//                    }
//                }
//            }
//        }
//
//        // Banner Ad at the bottom
//        UnityBannerAd(
//            placementId = "Banner_Android",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp)
//        )
//    }
//}
//
//@Composable
//fun UnityBannerAd(
//    placementId: String,
//    modifier: Modifier = Modifier
//) {
//    // Platform-specific banner ad implementation
//    PlatformBannerAd(
//        placementId = placementId,
//        modifier = modifier
//    )
//}
//
//// This will be implemented differently for each platform
//@Composable
//expect fun PlatformBannerAd(
//    placementId: String,
//    modifier: Modifier = Modifier
//)
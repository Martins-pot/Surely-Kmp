package com.sportmaster.surelykmp.activities.premiumcodes.presentation.screens

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.sportmaster.surelykmp.activities.freecodes.presentation.components.CodeItem
//import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
//import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
//
//@Composable
//fun CodesScreenPremium(
//    viewModel: CodesViewModel
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Header
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 20.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "CODES",
//                color = Color.White,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold
//            )
//
////            Row {
////                Icon(
////                    imageVector = Icons.Default.DateRange,
////                    contentDescription = "Calendar",
////                    tint = Color.White,
////                    modifier = Modifier.padding(end = 12.dp)
////                )
////                Icon(
////                    imageVector = Icons.Default.Star,
////                    contentDescription = "Crown",
////                    tint = Color(0xFFFFD700)
////                )
////            }
//        }
//
//        // Sport Tabs
//        SportTabSelector(
//            selectedSport = viewModel.selectedSport,
//            onSportSelected = { viewModel.selectSport(it) }
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Content
//        when {
//            viewModel.isLoading -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(color = Color(0xFFE53935))
//                }
//            }
//            viewModel.error != null -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = viewModel.error!!,
//                            color = Color.White,
//                            fontSize = 16.sp
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(
//                            onClick = { /* Retry logic */ },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFFE53935)
//                            )
//                        ) {
//                            Text("Retry")
//                        }
//                    }
//                }
//            }
//            viewModel.codes.isEmpty() -> {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "No codes available for ${viewModel.selectedSport.displayName}",
//                        color = Color.White,
//                        fontSize = 16.sp
//                    )
//                }
//            }
//            else -> {
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(viewModel.codes.filter {it.isExpensive }) { code ->
//                        CodeItem(
//                            code = code,
//                            onShare = {},
//                            onItemClick = {})
//                    }
//                }
//            }
//        }
//    }
//}





import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.PremiumCodesViewModel
import com.sportmaster.surelykmp.utils.UnityAdsManager
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.background_texture

@Composable
fun CodesScreenPremium(
    viewModel: PremiumCodesViewModel,
    onCodeClick: (Code) -> Unit = {}
) {
    val premiumState by viewModel.premiumState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    text = "PREMIUM CODES",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sport Tabs
            SportTabSelector(
                selectedSport = viewModel.selectedSport,
                onSportSelected = { viewModel.selectSport(it)
                },
                modifier = Modifier
                        .padding(horizontal = 16.dp)
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
                        modifier = Modifier.fillMaxSize()
                        .then(
                                if (premiumState.isBlurActive && !premiumState.isSubscribed && !premiumState.isTimerActive && !viewModel.isLoading
                                    &&
                                    viewModel.error == null && viewModel.codes.isNotEmpty())
                                    Modifier.blur(radius = 2.dp)
                                else Modifier
                                ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Spacer(
                                modifier = Modifier.height(70.dp)
                            )
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (premiumState.isBlurActive && !premiumState.isSubscribed && !premiumState.isTimerActive && !viewModel.isLoading
                                    &&
                                    viewModel.error == null && viewModel.codes.isNotEmpty())
                                    Modifier.blur(radius = 5.dp)
                                else Modifier
                            )
                    ) {
                        if (premiumState.isBlurActive && !premiumState.isSubscribed) {
                        Image(
                            painter = painterResource(Res.drawable.background_texture),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )}
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
//                        modifier = Modifier
//                            .then(
//                                if (premiumState.isBlurActive && !premiumState.isSubscribed)
//                                    Modifier.blur(radius = 5.dp)
//                                else Modifier
//                            )

                    ) {
                        items(viewModel.codes) { code ->
                            CodeItem(
                                code = code,
                                onShare = {},
                                onItemClick = { onCodeClick(code) },

                            )
                        }
                    }
                }
            }
            }
        }

        if (premiumState.isBlurActive && !premiumState.isSubscribed && !premiumState.isTimerActive && !viewModel.isLoading &&
        viewModel.error == null && viewModel.codes.isNotEmpty()) {
            // Match the LazyColumn’s area: here we just overlay the whole Box but
            // we allow touches to “pass through” except on the card itself.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {} // consume clicks to block LazyColumn
            ) {
                // Center the upgrade card
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 16.dp),
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
}
//        // Blur overlay with upgrade dialog
//        if (premiumState.isBlurActive && !premiumState.isSubscribed) {
//            BlurOverlay(
//                onWatchAd = {
//                    viewModel.onWatchAdClicked()
//                },
//                onUpgrade = {
//                    // Handle upgrade navigation
//                }
//            )
//        }
//    }
//}


@Composable
private fun PremiumDialogContent(
    onWatchAd: () -> Unit,
    onUpgrade: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(25.dp)
            .wrapContentSize()
            .border(
                width = .5.dp,
                color = Color.White.copy(.4f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                ,
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
                // Crown icon
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unlock",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                )

                // Title
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

                // Description
                Text(
                    text = "Expert codes are locked for free users",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Watch ads button
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

//            // Upgrade button
//            Button(
//                onClick = onUpgrade,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White
//                ),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Star,
//                    contentDescription = "Crown",
//                    tint = Color.Black,
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//                Text(
//                    text = "Upgrade to pro for just ₦1800",
//                    color = Color.Black,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
            }
        }
    }

}

@Composable
private fun BlurOverlay(
    onWatchAd: () -> Unit,
    onUpgrade: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crown icon
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Premium",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 16.dp)
                )

                // Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Upgrade",
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
                            text = "PRO",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Pro users already enjoy exclusive access to today's Expert Codes.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Watch ads button
                Button(
                    onClick = onWatchAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A4A4A)
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

                // Upgrade button
                Button(
                    onClick = onUpgrade,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Crown",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Upgrade to pro for just ₦1800",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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
        minutes in 3..5 -> Color(0xFFFF9500) // Orange
        else -> Color(0xFFD52127) // Red
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
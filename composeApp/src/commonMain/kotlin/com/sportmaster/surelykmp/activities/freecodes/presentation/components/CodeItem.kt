package com.sportmaster.surelykmp.activities.freecodes.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Icon_comment
import surelykmp.composeapp.generated.resources.Res
import kotlin.math.round

@Composable
fun CodeItem(
    code: Code,
    onItemClick: (Code) -> Unit,
    onShare: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    // Animation states for copy and share buttons
    var copyScale by remember { mutableStateOf(1f) }
    var shareScale by remember { mutableStateOf(1f) }

    val copyAnimation = animateFloatAsState(
        targetValue = copyScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val shareAnimation = animateFloatAsState(
        targetValue = shareScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable { onItemClick(code) }
    ) {
        if (code.platform == "foresport") {
            PredictionTypeLayout(
                code = code,
                onCopy = { text ->
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                    snackbarMessage = "$text copied"
                    showSnackbar = true

                    // Trigger animation
                    copyScale = 0.7f
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(100)
                        copyScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        copyScale = 0.7f
                        kotlinx.coroutines.delay(100)
                        copyScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        copyScale = 1f
                    }
                },
                onShare = { text ->
                    onShare(text)

                    // Trigger animation
                    shareScale = 0.7f
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(100)
                        shareScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        shareScale = 0.7f
                        kotlinx.coroutines.delay(100)
                        shareScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        shareScale = 1f
                    }
                },
                copyScale = copyAnimation.value,
                shareScale = shareAnimation.value
            )
        } else {
            CodeTypeLayout(
                code = code,
                onCopy = { text ->
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                    snackbarMessage = "$text copied"
                    showSnackbar = true

                    // Trigger animation
                    copyScale = 0.7f
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(100)
                        copyScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        copyScale = 0.7f
                        kotlinx.coroutines.delay(100)
                        copyScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        copyScale = 1f
                    }
                },
                onShare = { text ->
                    onShare(text)

                    // Trigger animation
                    shareScale = 0.7f
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(100)
                        shareScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        shareScale = 0.7f
                        kotlinx.coroutines.delay(100)
                        shareScale = 1.2f
                        kotlinx.coroutines.delay(100)
                        shareScale = 1f
                    }
                },
                copyScale = copyAnimation.value,
                shareScale = shareAnimation.value
            )
        }

        // Bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.2.dp)
                .alpha(0.5f)
                .background(Color(0xFF6B6B6B)) // calender_bar color
        )
    }

    // Show snackbar
    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            kotlinx.coroutines.delay(2000)
            showSnackbar = false
        }
    }
}

@Composable
private fun CodeTypeLayout(
    code: Code,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    copyScale: Float,
    shareScale: Float
) {
    Column {
        // Source accuracy section
        if (code.accuracy != null && code.isExpensive) {
            AccuracySection(
                accuracy = code.accuracy,
                modifier = Modifier.padding( vertical = 10.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left side content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Platform and country row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlatformImage(platform = code.platform)

                    Spacer(modifier = Modifier.width(10.dp))

                    CountryFlag(country = code.country)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Code text
                Text(
                    text = code.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom action buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy button
                    Image(

                        imageVector = CopyIcon(), // You'll need to implement these icons
                        contentDescription = "Copy",
                        modifier = Modifier
                            .scale(copyScale)
                            .clickable { onCopy(code.text) },
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)


                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Share button
                    Image(
                        imageVector = ShareIcon(),
                        contentDescription = "Share",
                        modifier = Modifier
                            .scale(shareScale)
                            .clickable { onShare(code.text) },
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Comment button with count
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        CommentIcon()


                        Spacer(modifier = Modifier.width(5.dp))

//                        Text(
//                            text = code.comments.size.toString(),
//                            style = TextStyle(
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.Medium,
//                                color = Color(0xFF9E9E9E) // calender_text color
//                            )
//                        )
                    }
                }
            }

            // Right side content
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Odds
                Text(
                    text = "${code.odds} odds",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = StarIcon(),
                        contentDescription = "Rating",
                        modifier = Modifier.size(15.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Yellow)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "${(round(code.rating * 10) / 10)}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time
                code.createdAt?.let { createdAt ->
                    Text(
                        text = getTimeAgo(createdAt),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9E9E9E) // calender_text color
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PredictionTypeLayout(
    code: Code,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    copyScale: Float,
    shareScale: Float
) {
    Column(
        modifier = Modifier.padding( vertical = 20.dp)
    ) {
        // Top row with rating and date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = StarIcon(),
                    contentDescription = "Rating",
                    modifier = Modifier.size(15.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Yellow)
                )

                Text(
                    text = "${(round(code.rating * 10) / 10)}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }

            // Date
            code.expirationDate?.let { date ->
                Text(
                    text = formatDate(date),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.End
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Accuracy section (if applicable)
        if (code.accuracy != null && code.isExpensive) {
            AccuracySection(accuracy = code.accuracy)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Prediction section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    Color.White.copy(.04f), // prediction_bg color
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(.24f),
                    shape = RoundedCornerShape(8.dp) // outer rounded border
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prediction type (country)
                Text(
                    text = code.country ?: "",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E9E9E)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Prediction text
                Text(
                    text = code.text,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Odds
                Text(
                    text = "${code.odds} odds",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Teams
        if (code.team1 != null && code.team2 != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = code.team1,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = code.team2,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
        }

        // Bottom row with actions and time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedCode = "${code.team1 ?: ""} vs ${code.team2 ?: ""} * ${code.text}"

                // Copy button
                Image(
                    imageVector = CopyIcon(),
                    contentDescription = "Copy",
                    modifier = Modifier
                        .scale(copyScale)
                        .clickable { onCopy(formattedCode) },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Share button
                Image(
                    imageVector = ShareIcon(),
                    contentDescription = "Share",
                    modifier = Modifier
                        .scale(shareScale)
                        .clickable { onShare(formattedCode) },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Comment button with count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    CommentIcon()
                    Spacer(modifier = Modifier.width(5.dp))

//                    Text(
//                        text = code.comments.size.toString(),
//                        style = TextStyle(
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Medium,
//                            color = Color(0xFF9E9E9E)
//                        )
//                    )
                }
            }

            // Time
            code.createdAt?.let { createdAt ->
                Text(
                    text = getTimeAgo(createdAt),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E9E9E)
                    )
                )
            }
        }
    }
}

@Composable
private fun AccuracySection(
    accuracy: Int,
    modifier: Modifier = Modifier
) {
    val isHighAccuracy = accuracy >= 70
    val backgroundColor = if (isHighAccuracy) Color(0x3321BC22) else Color(0x33FF9500)
    val textColor = if (isHighAccuracy) Color(0xFF21BC22) else Color(0xFFFF9500)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(backgroundColor.copy(.1f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Source accuracy: $accuracy%",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        )
    }
}

@Composable
private fun PlatformImage(platform: String?) {
    // This is a placeholder - you'll need to implement platform-specific images
    // For now, showing platform text if no specific image is available
    when (platform) {
        "sportybet" -> {
            // SportyBet logo placeholder
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(23.dp)
                    .background(Color.Gray, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SportyBet",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = Color.White
                    )
                )
            }
        }
        else -> {
            Text(
                text = platform ?: "",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7F7F7F)
                )
            )
        }
    }
}

@Composable
private fun CountryFlag(country: String?) {
    // Placeholder for country flags - implement with actual flag images
    Box(
        modifier = Modifier
            .size(15.dp)
            .background(Color.Gray, androidx.compose.foundation.shape.CircleShape)
    )
}

// Helper functions
private fun getTimeAgo(serverTime: String): String {
    return try {
        val instant = Instant.parse(serverTime)
        val currentInstant = Clock.System.now()
        val diffInSeconds = (currentInstant - instant).inWholeSeconds
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24

        when {
            diffInSeconds < 60 -> "$diffInSeconds second${if (diffInSeconds == 1L) "" else "s"} ago"
            diffInMinutes < 60 -> "$diffInMinutes minute${if (diffInMinutes == 1L) "" else "s"} ago"
            diffInHours < 24 -> "$diffInHours hour${if (diffInHours == 1L) "" else "s"} ago"
            else -> "$diffInDays day${if (diffInDays == 1L) "" else "s"} ago"
        }
    } catch (e: Exception) {
        "Unknown time"
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        dateString
    }
}

// Placeholder icon composables - replace with your actual icons
@Composable
private fun CopyIcon() = androidx.compose.material.icons.Icons.Default.ContentCopy

@Composable
private fun ShareIcon() = androidx.compose.material.icons.Icons.Default.Share

//@Composable
//private fun CommentIcon(){
//    Image(
//        painter = painterResource(Res.drawable.Icon_comment), // 🔹 replace with your drawable name
//        contentDescription = "Comment",
//        modifier = Modifier.size(20.dp) // optional: adjust size
//    )
//}
//} = androidx.compose.material.icons.Icons.Default.Comment.tintColor

@Composable
private fun StarIcon() = androidx.compose.material.icons.Icons.Default.Star



//@Composable
//fun CodeItem(
//    code: Code,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp)),
//        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFF2A2A2A)
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // Header with username and odds
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = code.username ?: "",
//                        color = Color(0xFF888888),
//                        fontSize = 14.sp
//                    )
//
//                    // Online indicator
//                    Box(
//                        modifier = Modifier
//                            .size(8.dp)
//                            .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
//                    )
//                }
//
//                Text(
//                    text = "${code.odds} odds",
//                    color = Color.White,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Code text
//            Text(
//                text = code.text,
//                color = Color.White,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Rating and actions
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Star,
//                        contentDescription = "Rating",
//                        tint = Color(0xFFFFD700),
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Text(
//                        text = code.rating.toString(),
//                        color = Color.White,
//                        fontSize = 14.sp
//                    )
//                }
//
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Phone,
//                        contentDescription = "Phone",
//                        tint = Color(0xFF888888),
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Icon(
//                        imageVector = Icons.Default.Edit,
//                        contentDescription = "Edit",
//                        tint = Color(0xFF888888),
//                        modifier = Modifier.size(20.dp)
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(4.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Comment,
//                            contentDescription = "Comments",
//                            tint = Color(0xFF888888),
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Text(
//                            text = "1268",
//                            color = Color(0xFF888888),
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Time
//            Text(
//                text = "27 hrs",
//                color = Color(0xFF888888),
//                fontSize = 12.sp,
//                modifier = Modifier.align(Alignment.End)
//            )
//        }
//    }
//}
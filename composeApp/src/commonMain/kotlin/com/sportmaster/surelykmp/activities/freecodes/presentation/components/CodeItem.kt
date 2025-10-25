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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources._xbet
import surelykmp.composeapp.generated.resources.bet9ja
import surelykmp.composeapp.generated.resources.betfigo
import surelykmp.composeapp.generated.resources.betking_me
import surelykmp.composeapp.generated.resources.betway
import surelykmp.composeapp.generated.resources.cameroon
import surelykmp.composeapp.generated.resources.comment_icon
import surelykmp.composeapp.generated.resources.copy_icon
import surelykmp.composeapp.generated.resources.ghana
import surelykmp.composeapp.generated.resources.kenya
import surelykmp.composeapp.generated.resources.livescorebet
import surelykmp.composeapp.generated.resources.megapari
import surelykmp.composeapp.generated.resources.ng234bet
import surelykmp.composeapp.generated.resources.nigeria
import surelykmp.composeapp.generated.resources.share_icon
import surelykmp.composeapp.generated.resources.south_africa
import surelykmp.composeapp.generated.resources.sportytext
import surelykmp.composeapp.generated.resources.star_icon
import surelykmp.composeapp.generated.resources.tanzania
import surelykmp.composeapp.generated.resources.uganda
import surelykmp.composeapp.generated.resources.world
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
    var copyAnimating by remember { mutableStateOf(false) }
    var shareAnimating by remember { mutableStateOf(false) }

    val copyScale by animateFloatAsState(
        targetValue = if (copyAnimating) 0.7f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val shareScale by animateFloatAsState(
        targetValue = if (shareAnimating) 0.7f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Function to handle copy with animation
    fun handleCopy(text: String) {
        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
        snackbarMessage = "Code copied"
        showSnackbar = true

        // Trigger animation
        copyAnimating = true
    }

    // Function to handle share with animation
    fun handleShare(text: String) {
        onShare(text)

        // Trigger animation
        shareAnimating = true
    }

    // Reset copy animation after it completes
    LaunchedEffect(copyAnimating) {
        if (copyAnimating) {
            delay(300) // Shorter animation duration
            copyAnimating = false
        }
    }

    // Reset share animation after it completes
    LaunchedEffect(shareAnimating) {
        if (shareAnimating) {
            delay(300) // Shorter animation duration
            shareAnimating = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .clickable(
                indication = null, // This removes the ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onItemClick(code) }
    ) {
        if (code.platform == "foresport") {
            PredictionTypeLayout(
                code = code,
                onCopy = { text -> handleCopy(text) },
                onShare = { text -> handleShare(text) },
                copyScale = copyScale,
                shareScale = shareScale
            )
        } else {
            CodeTypeLayout(
                code = code,
                onCopy = { text -> handleCopy(text) },
                onShare = { text -> handleShare(text) },
                copyScale = copyScale,
                shareScale = shareScale
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.2.dp)
                .alpha(0.5f)
                .background(Color(0xFF6B6B6B))
        )
    }

    // Show snackbar
    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            delay(2000)
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
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
    ) {
        // Source accuracy section
        if (code.accuracy != null && code.isExpensive) {
            AccuracySection(
                accuracy = code.accuracy,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
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
                    CopyIcon(
                        onCopy = { onCopy(code.text) },
                        scale = copyScale
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Share button
                    ShareIcon(
                        onShare = { onShare(code.text) },
                        scale = shareScale
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Comment button with count
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CommentIcon()

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = code.comments.size.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF9E9E9E)
                            )
                        )
                    }
                }
            }

            // Right side content
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Odds
                Text(
                    text = "${(round(code.odds * 10) / 10)} odds",
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
                        painter = painterResource(Res.drawable.star_icon),
                        contentDescription = "Rating",
                        modifier = Modifier.size(15.dp),
//                        colorFilter = ColorFilter.tint(Color.Yellow)
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
                            color = Color(0xFF9E9E9E)
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
    // Create the same formatted text for both copy and share
    val formattedCode = if (code.team1 != null && code.team2 != null) {
        "${code.team1} vs ${code.team2} * ${code.text}"
    } else {
        code.text
    }

    Column(
        modifier = Modifier.padding(20.dp)
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
                    painter = painterResource(Res.drawable.copy_icon), // Using star icon if available
                    contentDescription = "Rating",
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(Color.Yellow)
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
                    Color.White.copy(.04f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(.24f),
                    shape = RoundedCornerShape(8.dp)
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
                    text = "${(round(code.odds * 10) / 10)} odds",
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
                // Copy button - uses formattedCode
                CopyIcon(
                    onCopy = { onCopy(formattedCode) },
                    scale = copyScale
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Share button - uses the SAME formattedCode as copy
                ShareIcon(
                    onShare = { onShare(formattedCode) },
                    scale = shareScale
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Comment button with count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CommentIcon()
                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = code.comments.size.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9E9E9E)
                        )
                    )
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
    val grayFilter = ColorFilter.tint(Color.Gray)

    when (platform?.lowercase()) {
        "1xbet" -> Image(
            painter = painterResource(Res.drawable._xbet),
            contentDescription = "1xbet",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "sportybet" -> Image(
            painter = painterResource(Res.drawable.sportytext),
            contentDescription = "SportyBet",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "bet9ja" -> Image(
            painter = painterResource(Res.drawable.bet9ja),
            contentDescription = "Bet9ja",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "betking" -> Image(
            painter = painterResource(Res.drawable.betking_me),
            contentDescription = "Betking",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "betway" -> Image(
            painter = painterResource(Res.drawable.betway),
            contentDescription = "Betway",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "livescorebet" -> Image(
            painter = painterResource(Res.drawable.livescorebet),
            contentDescription = "Livescorebet",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "megapari" -> Image(
            painter = painterResource(Res.drawable.megapari),
            contentDescription = "Megapari",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "betfigo" -> Image(
            painter = painterResource(Res.drawable.betfigo),
            contentDescription = "BetFigo",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        "ng234bet" -> Image(
            painter = painterResource(Res.drawable.ng234bet),
            contentDescription = "NG234Bet",
            modifier = Modifier.size(width = 80.dp, height = 23.dp),
            colorFilter = grayFilter
        )
        else -> {
            if (!platform.isNullOrBlank()) {
                Text(
                    text = platform,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7F7F7F)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CountryFlag(country: String?) {
    val painter = when (country) {
        "Nigeria" -> painterResource(Res.drawable.nigeria)
        "Cameroon" -> painterResource(Res.drawable.cameroon)
        "Ghana" -> painterResource(Res.drawable.ghana)
        "Kenya" -> painterResource(Res.drawable.kenya)
        "South Africa" -> painterResource(Res.drawable.south_africa)
        "Tanzania" -> painterResource(Res.drawable.tanzania)
        "Uganda" -> painterResource(Res.drawable.uganda)
        else -> painterResource(Res.drawable.world)
    }

    Image(
        painter = painter,
        contentDescription = country ?: "World",
        modifier = Modifier.size(16.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun CopyIcon(onCopy: () -> Unit, scale: Float) {
    Image(
        painter = painterResource(Res.drawable.copy_icon),
        contentDescription = "Copy",
        modifier = Modifier
            .size(22.dp)
            .scale(scale)
            .clickable(
                indication = null, // Remove ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onCopy() }
    )
}

@Composable
private fun ShareIcon(onShare: () -> Unit, scale: Float) {
    Image(
        painter = painterResource(Res.drawable.share_icon),
        contentDescription = "Share",
        modifier = Modifier
            .size(22.dp)
            .scale(scale)
            .clickable(
                indication = null, // Remove ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onShare() }
    )
}

@Composable
private fun CommentIcon() {
    Image(
        painter = painterResource(Res.drawable.comment_icon),
        contentDescription = "Comment",
        modifier = Modifier
            .size(22.dp)
            .clickable(
                indication = null, // Remove ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { /* Handle comment click */ }
    )
}

@Composable
private fun StarIcon() {
    Image(
        painter = painterResource(Res.drawable.star_icon),
        contentDescription = "Comment",
        modifier = Modifier
            .size(16.dp)
            .clickable(
                indication = null, // Remove ripple effect
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { /* Handle comment click */ }
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
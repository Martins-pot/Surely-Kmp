package com.sportmaster.surelykmp.activities.matches.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionItem
import com.sportmaster.surelykmp.activities.matches.data.models.PredictionResponse
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.UiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPredictionsScreen(
    matchId: String,
    onBackClick: () -> Unit,
    isSubscribed: Boolean = false,
    onSubscribeClick: () -> Unit = {},
    viewModel: MatchesViewModel = viewModel()
) {
    val predictionState by viewModel.predictionState.collectAsState()
    val matchesState by viewModel.matchesState.collectAsState()

    // Find the match from the current matches state
    val match = remember(matchesState, matchId) {
        when (matchesState) {
            is UiState.Success -> (matchesState as UiState.Success<List<MatchItem>>).data.find { it.id == matchId }
            else -> null
        }
    }

    LaunchedEffect(matchId) {
        viewModel.loadPrediction(matchId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "AI PREDICTIONS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Match Info Card
            match?.let {
                MatchInfoCard(match = it)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Subscription Banner (if not subscribed)
            if (!isSubscribed) {
                SubscriptionBanner(
                    onSubscribeClick = onSubscribeClick,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Predictions Content
            when (predictionState) {
                is UiState.Loading -> {
                    PredictionsLoadingState()
                }
                is UiState.Error -> {
                    PredictionsErrorState(
                        onRetry = { viewModel.loadPrediction(matchId) }
                    )
                }
                is UiState.Success -> {
                    match?.let { matchData ->
                        PredictionsList(
                            predictions = buildPredictionsList(
                                (predictionState as UiState.Success<PredictionResponse>).data.prediction,
                                matchData.sport
                            ),
                            isSubscribed = isSubscribed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchInfoCard(match: MatchItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // League and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatMatchDate(match.matchDate),
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teams
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInfoDetailed(
                    teamName = match.homeTeam,
                    teamImageUrl = match.homeTeamImage,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "VS",
                    color = Color(0xFF666666),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                TeamInfoDetailed(
                    teamName = match.awayTeam,
                    teamImageUrl = match.awayTeamImage,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TeamInfoDetailed(
    teamName: String,
    teamImageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Team Image Placeholder
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    Color(0xFF444444).copy(alpha = 0.8f),
                    RoundedCornerShape(25.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamName.take(2).uppercase(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = teamName,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SubscriptionBanner(
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D4A87).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI SPORTS SAYS",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upgrade to Premium for full predictions",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onSubscribeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "GET PREMIUM",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PredictionsList(
    predictions: List<PredictionItem>,
    isSubscribed: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(predictions) { prediction ->
            PredictionCard(
                prediction = prediction,
                isBlurred = !isSubscribed && prediction.title != "Outcome"
            )
        }
    }
}

@Composable
private fun PredictionCard(
    prediction: PredictionItem,
    isBlurred: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${prediction.title.uppercase()} PREDICTION",
                color = Color(0xFF888888),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = if (isBlurred) {
                    Modifier.blur(4.dp)
                } else Modifier
            ) {
                Text(
                    text = prediction.prediction,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun PredictionsLoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Color(0xFF1E88E5),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Analyzing match data...",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please wait while our AI processes the predictions",
            color = Color(0xFF888888),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PredictionsErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Failed to load predictions",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please check your connection and try again",
            color = Color(0xFF888888),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Retry",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun buildPredictionsList(prediction: Any, sport: String): List<PredictionItem> {
    // This should be replaced with your actual Prediction data class
    // For now, returning sample data
    return when (sport.lowercase()) {
        "football" -> listOf(
            PredictionItem("Outcome", "Arsenal FC win 55%, Draw 25%, Chelsea FC win 20%"),
            PredictionItem("Offsides", "Arsenal FC 2.1, Chelsea FC 1.8"),
            PredictionItem("Possession", "Arsenal FC 58%, Chelsea FC 42%"),
            PredictionItem("Discipline", "Arsenal FC 1.2 yellow cards, Chelsea FC 1.8 yellow cards"),
            PredictionItem("Passing Accuracy", "Arsenal FC 85%, Chelsea FC 82%"),
            PredictionItem("Shooting", "Arsenal FC 14 shots, Chelsea FC 11 shots"),
            PredictionItem("Corners", "Arsenal FC 6.2, Chelsea FC 4.8")
        )
        "basketball" -> listOf(
            PredictionItem("Outcome", "Team A win 60%, Team B win 40%"),
            PredictionItem("Points", "Team A 105, Team B 98"),
            PredictionItem("3-Pointers", "Team A 12, Team B 9"),
            PredictionItem("Rebounds", "Team A 45, Team B 42"),
            PredictionItem("Fouls", "Team A 18, Team B 21"),
            PredictionItem("Assists", "Team A 24, Team B 20"),
            PredictionItem("Free Throws", "Team A 78%, Team B 82%"),
            PredictionItem("Steals", "Team A 8, Team B 6"),
            PredictionItem("Blocks", "Team A 5, Team B 7")
        )
        "tennis" -> listOf(
            PredictionItem("Outcome", "Player A win 65%, Player B win 35%"),
            PredictionItem("Aces", "Player A 12, Player B 8"),
            PredictionItem("Double Faults", "Player A 3, Player B 5"),
            PredictionItem("Break Points", "Player A 6/8, Player B 3/6"),
            PredictionItem("Rally Length", "Average 4.2 shots per rally"),
            PredictionItem("Sets Prediction", "3-1 to Player A")
        )
        else -> emptyList()
    }
}

private fun formatMatchDate(dateString: String): String {
    return try {
        dateString.substringBefore("T").let { date ->
            val time = dateString.substringAfter("T").substringBefore(":")
            "$date | $time:00"
        }
    } catch (e: Exception) {
        "TBD"
    }
}
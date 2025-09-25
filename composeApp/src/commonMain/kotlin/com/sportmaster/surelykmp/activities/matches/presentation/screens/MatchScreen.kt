package com.sportmaster.surelykmp.activities.matches.presentation.screens


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.data.repository.MatchesRepository
import com.sportmaster.surelykmp.activities.matches.presentation.components.SportTabSelectorMatches
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.UiState
import com.sportmaster.surelykmp.core.presentation.screens.Screen
import org.koin.compose.koinInject

@Composable
fun MatchesScreen(
    navController: NavController,
    viewModel: MatchesViewModel = koinInject()
) {
    val selectedSport by viewModel.selectedSport.collectAsState()
    val matchesState by viewModel.matchesState.collectAsState()

    // Get filtered matches for display
    val displayMatches = remember(matchesState, selectedSport) {
        when (val currentState = matchesState) {
            is UiState.Success -> currentState.data.filter {
                it.sport.equals(selectedSport.apiName, ignoreCase = true)
            }
            else -> emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "AI SPORTS",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Sport Selector
        SportTabSelectorMatches(
            selectedSport = selectedSport,
            onSportSelected = { viewModel.selectSport(it) },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Matches List
        when (val currentState = matchesState) {
            is UiState.Loading -> {
                LoadingState()
            }
            is UiState.Error -> {
                ErrorMessage(
                    message = "Failed to load matches",
                    onRetry = { viewModel.loadMatches() }
                )
            }
            is UiState.Success -> {
                if (displayMatches.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayMatches) { match ->
                            MatchCard(
                                match = match,
                                onClick = {
                                    println("DEBUG: Navigating to predictions for match - ID: ${match.id}, Sport: '${match.sport}', Teams: ${match.homeTeam} vs ${match.awayTeam}")
                                    navController.navigate("predictions/${match.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Make sure your GetMatchesUseCase.execute() method exists
class GetMatchesUseCase(private val repository: MatchesRepository) {

    // Get all matches (for lookup purposes)
    suspend fun execute(): List<MatchItem> {
        return repository.getMatches()
    }

    // Get matches filtered by sport (for display)
    suspend fun executeForSport(sport: String): List<MatchItem> {
        val allMatches = repository.getMatches()
        return repository.getMatchesBySport(allMatches, sport)
    }
}
@Composable
private fun MatchCard(
    match: MatchItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Teams
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInfo(
                    teamName = match.homeTeam,
                    teamImageUrl = match.homeTeamImage,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "VS",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                TeamInfo(
                    teamName = match.awayTeam,
                    teamImageUrl = match.awayTeamImage,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ask AI Button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF444444).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ASK AI PREDICTIONS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
private fun TeamInfo(
    teamName: String,
    teamImageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Load team image
        var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }

        val painter = rememberAsyncImagePainter(
            model = teamImageUrl,
            onSuccess = {
                val width = it.painter.intrinsicSize.width
                val height = it.painter.intrinsicSize.height
                imageLoadResult = if (width > 1 && height > 1) {
                    Result.success(it.painter)
                } else {
                    Result.failure(Exception("Invalid image size"))
                }
            },
            onError = {
                it.result.throwable.printStackTrace()
                imageLoadResult = Result.failure(it.result.throwable)
            }
        )

        when (val result = imageLoadResult) {
            null -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF444444).copy(alpha = 0.8f),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                }
            }
            else -> {
                Image(
                    painter = if (result.isSuccess) painter else {
                        // Fallback to team initials
                        ColorPainter(Color.Transparent)
                    },
                    contentDescription = "$teamName logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Color(0xFF444444).copy(alpha = 0.8f),
                            RoundedCornerShape(20.dp)
                        )
                )

                // Overlay team initials if image failed
                if (!result.isSuccess) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = teamName.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = teamName,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
        // Load team image for detailed view
        var imageLoadResult by remember { mutableStateOf<Result<Painter>?>(null) }

        val painter = rememberAsyncImagePainter(
            model = teamImageUrl,
            onSuccess = {
                val width = it.painter.intrinsicSize.width
                val height = it.painter.intrinsicSize.height
                imageLoadResult = if (width > 1 && height > 1) {
                    Result.success(it.painter)
                } else {
                    Result.failure(Exception("Invalid image size"))
                }
            },
            onError = {
                it.result.throwable.printStackTrace()
                imageLoadResult = Result.failure(it.result.throwable)
            }
        )

        when (val result = imageLoadResult) {
            null -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Color(0xFF444444).copy(alpha = 0.8f),
                            RoundedCornerShape(25.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }
            }
            else -> {
                Image(
                    painter = if (result.isSuccess) painter else {
                        ColorPainter(Color.Transparent)
                    },
                    contentDescription = "$teamName logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            Color(0xFF444444).copy(alpha = 0.8f),
                            RoundedCornerShape(25.dp)
                        )
                )

                // Overlay team initials if image failed
                if (!result.isSuccess) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(25.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = teamName.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading matches...",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF444444)
            )
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No matches available for this sport",
            color = Color(0xFF888888),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun formatMatchDate(dateString: String): String {
    return try {
        // Add your date formatting logic here
        // For now, return a simple format
        dateString.substringBefore("T").let { date ->
            val time = dateString.substringAfter("T").substringBefore(":")
            "$date | $time:00"
        }
    } catch (e: Exception) {
        "TBD"
    }
}
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sportmaster.surelykmp.activities.matches.data.models.MatchItem
import com.sportmaster.surelykmp.activities.matches.presentation.components.SportTabSelectorMatches
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.MatchesViewModel
import com.sportmaster.surelykmp.activities.matches.presentation.viewmodel.UiState
import com.sportmaster.surelykmp.core.presentation.screens.Screen


@Composable
fun MatchesScreen(
    navController: NavController, // Replace with your actual NavController type
    viewModel: MatchesViewModel = viewModel()
) {
    val selectedSport by viewModel.selectedSport.collectAsState()
    val matchesState by viewModel.matchesState.collectAsState()

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
        // Team Image Placeholder (you can replace with AsyncImage from Coil)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color(0xFF444444).copy(alpha = 0.8f),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamName.take(2).uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
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
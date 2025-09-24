package com.sportmaster.surelykmp.activities.matches.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.matches.data.models.SportMatch


@Composable
fun SportTabSelectorMatches(
    selectedSport: SportMatch,
    onSportSelected: (SportMatch) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFF2A2A2A),
                RoundedCornerShape(25.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SportMatch.values().forEach { sport ->
            SportTab(
                sport = sport,
                isSelected = sport == selectedSport,
                onClick = { onSportSelected(sport) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SportTab(
    sport: SportMatch,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        when (sport) {
            SportMatch.FOOTBALL -> Color(0xFFFF4444)
            SportMatch.BASKETBALL -> Color(0xFFFF8C00)
            SportMatch.TENNIS -> Color(0xFF4CAF50)
        }
    } else Color.Transparent

    val textColor = if (isSelected) Color.White else Color(0xFF888888)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sport.displayName,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
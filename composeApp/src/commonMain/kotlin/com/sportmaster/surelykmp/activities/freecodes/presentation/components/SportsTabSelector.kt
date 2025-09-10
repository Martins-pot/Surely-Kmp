package com.sportmaster.surelykmp.activities.freecodes.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.selected_basketball
import surelykmp.composeapp.generated.resources.selected_football
import surelykmp.composeapp.generated.resources.selected_tennis
import surelykmp.composeapp.generated.resources.unselected_basketball
import surelykmp.composeapp.generated.resources.unselected_football
import surelykmp.composeapp.generated.resources.unselected_tennis


@Composable
fun SportTabSelector(
    selectedSport: Sport,
    onSportSelected: (Sport) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Sport.values().forEach { sport ->
            val isSelected = sport == selectedSport

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .background(
                        color = if (isSelected) Color(0xFFE53935) else Color(0xFF2A2A2A),
                        shape = when (sport) {
                            Sport.FOOTBALL -> RoundedCornerShape(
                                topStart = 12.dp,
                                bottomStart = 12.dp
                            )
                            Sport.TENNIS -> RoundedCornerShape(
                                topEnd = 12.dp,
                                bottomEnd = 12.dp
                            )
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onSportSelected(sport) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = when (sport) {
                            Sport.FOOTBALL -> if (isSelected) {
                                painterResource(Res.drawable.selected_football)
                            } else {
                                painterResource(Res.drawable.unselected_football)
                            }
                            Sport.BASKETBALL -> if (isSelected) {
                                painterResource(Res.drawable.selected_basketball)
                            } else {
                                painterResource(Res.drawable.unselected_basketball)
                            }
                            Sport.TENNIS -> if (isSelected) {
                                painterResource(Res.drawable.selected_tennis)
                            } else {
                                painterResource(Res.drawable.unselected_tennis)
                            }
                        },
                        contentDescription = sport.displayName,
                        modifier = Modifier
                            .size(36.dp)
                    )
//                    Text(
//                        text = when (sport) {
//                            Sport.FOOTBALL -> "⚽"
//                            Sport.BASKETBALL -> "🏀"
//                            Sport.TENNIS -> "🎾"
//                        },
//                        fontSize = 16.sp
//                    )

                    Text(
                        text = sport.displayName,
                        color = if (isSelected) Color.White else Color(0xFF888888),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
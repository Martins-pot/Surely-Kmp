package com.sportmaster.surelykmp.activities.matches.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.domain.model.Sport
import com.sportmaster.surelykmp.activities.matches.data.models.SportMatch
import org.jetbrains.compose.resources.painterResource
import surelykmp.composeapp.generated.resources.Res
import surelykmp.composeapp.generated.resources.selected_basketball
import surelykmp.composeapp.generated.resources.selected_football
import surelykmp.composeapp.generated.resources.selected_tennis
import surelykmp.composeapp.generated.resources.unselected_basketball
import surelykmp.composeapp.generated.resources.unselected_football
import surelykmp.composeapp.generated.resources.unselected_tennis


@Composable
fun SportTabSelectorMatches(
    selectedSport: SportMatch,
    onSportSelected: (SportMatch) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 2.dp,
                color = Color.White.copy(.24f),
                shape = RoundedCornerShape(10.dp) // outer rounded border
            )

    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            SportMatch.values().forEachIndexed { index, sport ->
                val isSelected = sport == selectedSport

                //  Animate colors
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFFD52127) else Color.Transparent,
                    animationSpec = tween(durationMillis = 600)
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Gray,
                    animationSpec = tween(durationMillis = 600)
                )

                //  Optional scale animation for subtle "pop"
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else .9f,
                    animationSpec = tween(durationMillis = 700)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = backgroundColor,
                            shape = when (sport) {
                                SportMatch.FOOTBALL -> RoundedCornerShape(
                                    topStart = 12.dp,
                                    bottomStart = 12.dp
                                )
                                SportMatch.TENNIS -> RoundedCornerShape(
                                    topEnd = 12.dp,
                                    bottomEnd = 12.dp
                                )
                                else -> RoundedCornerShape(0.dp)
                            }
                        )
                        .clickable { onSportSelected(sport) }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = when (sport) {
                                SportMatch.FOOTBALL -> if (isSelected) {
                                    painterResource(Res.drawable.selected_football)
                                } else {
                                    painterResource(Res.drawable.unselected_football)
                                }
                                SportMatch.BASKETBALL -> if (isSelected) {
                                    painterResource(Res.drawable.selected_basketball)
                                } else {
                                    painterResource(Res.drawable.unselected_basketball)
                                }
                                SportMatch.TENNIS -> if (isSelected) {
                                    painterResource(Res.drawable.selected_tennis)
                                } else {
                                    painterResource(Res.drawable.unselected_tennis)
                                }
                            },
                            contentDescription = sport.displayName,
                            modifier = Modifier.size(28.dp)
                        )

                        Text(
                            text = sport.displayName,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                //  Add vertical white dividers, but not after the last item
                if (index < Sport.values().lastIndex) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color.White.copy(.1f))
                    )
                }
            }
        }
    }
}
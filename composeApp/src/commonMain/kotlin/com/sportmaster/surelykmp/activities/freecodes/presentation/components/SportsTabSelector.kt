package com.sportmaster.surelykmp.activities.freecodes.presentation.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
            Sport.values().forEachIndexed { index, sport ->
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

package com.sportmaster.surelykmp.activities.freecodes.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    val shimmerColors = listOf(
        Color(0xFFB8B5B5).copy(alpha = 0.3f),
        Color(0xFF8F8B8B).copy(alpha = 0.7f),
        Color(0xFFB8B5B5).copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(startOffsetX, 0f),
        end = Offset(startOffsetX + 1f, 1f)
    )

    Box(
        modifier = modifier
            .background(brush = brush, shape = RoundedCornerShape(cornerRadius))
    )
}


@Composable
fun ShimmerCodeItem() {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerColor = Color(0xFF2A2A2A)
    val highlightColor = Color(0xFF3A3A3A)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Main content with padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Accuracy placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(vertical = 10.dp)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        shimmerColor,
                                        highlightColor,
                                        shimmerColor
                                    ),
                                    start = Offset(shimmerProgress * size.width, 0f),
                                    end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                ),
                                blendMode = BlendMode.Screen
                            )
                        }
                    }
                    .background(shimmerColor)
            )

            // Main content row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side
                Column(modifier = Modifier.weight(1f)) {
                    // Platform placeholder
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(20.dp)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                shimmerColor,
                                                highlightColor,
                                                shimmerColor
                                            ),
                                            start = Offset(shimmerProgress * size.width, 0f),
                                            end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                        ),
                                        blendMode = BlendMode.Screen
                                    )
                                }
                            }
                            .background(shimmerColor)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Code text placeholder
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(20.dp)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                shimmerColor,
                                                highlightColor,
                                                shimmerColor
                                            ),
                                            start = Offset(shimmerProgress * size.width, 0f),
                                            end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                        ),
                                        blendMode = BlendMode.Screen
                                    )
                                }
                            }
                            .background(shimmerColor)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Icons row
                    Row {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        shimmerColor,
                                                        highlightColor,
                                                        shimmerColor
                                                    ),
                                                    start = Offset(shimmerProgress * size.width, 0f),
                                                    end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                                ),
                                                blendMode = BlendMode.Screen
                                            )
                                        }
                                    }
                                    .background(shimmerColor)
                            )
                            if (it < 2) Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }

                // Right side
                Column(horizontalAlignment = Alignment.End) {
                    // Odds placeholder
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .height(20.dp)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                shimmerColor,
                                                highlightColor,
                                                shimmerColor
                                            ),
                                            start = Offset(shimmerProgress * size.width, 0f),
                                            end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                        ),
                                        blendMode = BlendMode.Screen
                                    )
                                }
                            }
                            .background(shimmerColor)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Rating row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    shimmerColor,
                                                    highlightColor,
                                                    shimmerColor
                                                ),
                                                start = Offset(shimmerProgress * size.width, 0f),
                                                end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                            ),
                                            blendMode = BlendMode.Screen
                                        )
                                    }
                                }
                                .background(shimmerColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    shimmerColor,
                                                    highlightColor,
                                                    shimmerColor
                                                ),
                                                start = Offset(shimmerProgress * size.width, 0f),
                                                end = Offset(shimmerProgress * size.width + 100.dp.toPx(), size.height)
                                            ),
                                            blendMode = BlendMode.Screen
                                        )
                                    }
                                }
                                .background(shimmerColor)
                        )
                    }
                }
            }
        }

        // Divider after every shimmer item - without shimmer effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.2.dp)
                .background(Color(0xFF6B6B6B).copy(alpha = 0.5f))
        )
    }
}
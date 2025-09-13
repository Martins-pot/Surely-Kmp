package com.sportmaster.surelykmp.activities.premiumcodes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.CodeItem
import com.sportmaster.surelykmp.activities.freecodes.presentation.components.SportTabSelector
import com.sportmaster.surelykmp.activities.freecodes.presentation.viewmodels.CodesViewModel

@Composable
fun CodesScreenPremium(
    viewModel: CodesViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CODES",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

//            Row {
//                Icon(
//                    imageVector = Icons.Default.DateRange,
//                    contentDescription = "Calendar",
//                    tint = Color.White,
//                    modifier = Modifier.padding(end = 12.dp)
//                )
//                Icon(
//                    imageVector = Icons.Default.Star,
//                    contentDescription = "Crown",
//                    tint = Color(0xFFFFD700)
//                )
//            }
        }

        // Sport Tabs
        SportTabSelector(
            selectedSport = viewModel.selectedSport,
            onSportSelected = { viewModel.selectSport(it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Content
        when {
            viewModel.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE53935))
                }
            }
            viewModel.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.error!!,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Retry logic */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            viewModel.codes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No codes available for ${viewModel.selectedSport.displayName}",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.codes.filter {it.isExpensive }) { code ->
                        CodeItem(
                            code = code,
                            onShare = {},
                            onItemClick = {})
                    }
                }
            }
        }
    }
}
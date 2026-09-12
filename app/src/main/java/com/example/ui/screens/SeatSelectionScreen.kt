package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onContinueToPassengerDetails: () -> Unit
) {
    val route by viewModel.selectedRoute.collectAsState()
    val bookedSeats by viewModel.bookedSeats.collectAsState()
    val selectedSeat by viewModel.selectedSeat.collectAsState()

    val origin = route?.origin ?: "Kharan"
    val destination = route?.destination ?: "Quetta"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Select Seat",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$origin → $destination",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_seats")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZaNavyDark)
            )
        },
        bottomBar = {
            // Sticky Bottom Bar matching reference image 5
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selected Seat",
                            fontSize = 12.sp,
                            color = ZaTextSecondary
                        )
                        Text(
                            text = if (selectedSeat != null) "Seat $selectedSeat" else "Tap any available seat",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSeat != null) ZaNavyDark else ZaTextMuted
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedSeat != null) {
                                onContinueToPassengerDetails()
                            }
                        },
                        enabled = selectedSeat != null,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("button_continue_seat"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZaBluePrimary,
                            disabledContainerColor = Color(0xFFCBD5E1)
                        )
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        containerColor = ZaBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Legend Row matching reference image 5
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Available Legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.5.dp, ZaGreenSuccess, RoundedCornerShape(6.dp))
                                    .background(ZaGreenLight.copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Available",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZaTextPrimary
                            )
                        }

                        // Booked Legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.5.dp, ZaRedDanger, RoundedCornerShape(6.dp))
                                    .background(ZaRedLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Booked",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZaTextPrimary
                            )
                        }

                        // Selected Legend
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ZaBluePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Selected",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ZaTextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 44-Seat Bus Grid (11 rows x 4 seats with central aisle) matching reference image 5
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (row in 1..11) {
                            val seat1 = (row - 1) * 4 + 1
                            val seat2 = (row - 1) * 4 + 2
                            val seat3 = (row - 1) * 4 + 3
                            val seat4 = (row - 1) * 4 + 4

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Row Number
                                Text(
                                    text = "$row",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ZaTextMuted,
                                    modifier = Modifier.width(22.dp)
                                )

                                // Left pair
                                SeatBox(
                                    seatNumber = seat1,
                                    isBooked = bookedSeats.contains(seat1),
                                    isSelected = selectedSeat == seat1,
                                    onSelect = { viewModel.selectSeatNumber(seat1) }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                SeatBox(
                                    seatNumber = seat2,
                                    isBooked = bookedSeats.contains(seat2),
                                    isSelected = selectedSeat == seat2,
                                    onSelect = { viewModel.selectSeatNumber(seat2) }
                                )

                                // Aisle (central divider)
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .fillMaxHeight()
                                            .background(Color(0xFFE2E8F0))
                                    )
                                }

                                // Right pair
                                SeatBox(
                                    seatNumber = seat3,
                                    isBooked = bookedSeats.contains(seat3),
                                    isSelected = selectedSeat == seat3,
                                    onSelect = { viewModel.selectSeatNumber(seat3) }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                SeatBox(
                                    seatNumber = seat4,
                                    isBooked = bookedSeats.contains(seat4),
                                    isSelected = selectedSeat == seat4,
                                    onSelect = { viewModel.selectSeatNumber(seat4) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeatBox(
    seatNumber: Int,
    isBooked: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor = when {
        isSelected -> ZaBluePrimary
        isBooked -> ZaRedLight
        else -> ZaGreenLight.copy(alpha = 0.35f)
    }

    val borderColor = when {
        isSelected -> ZaBluePrimary
        isBooked -> ZaRedDanger
        else -> ZaGreenSuccess
    }

    val textColor = when {
        isSelected -> Color.White
        isBooked -> ZaRedDanger
        else -> ZaGreenSuccess
    }

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(enabled = !isBooked, onClick = onSelect)
            .testTag("seat_$seatNumber"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$seatNumber",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

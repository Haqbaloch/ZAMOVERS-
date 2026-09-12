package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerBookingScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val route by viewModel.selectedRoute.collectAsState()
    val seatNumber by viewModel.selectedSeat.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val travelDate by viewModel.travelDate.collectAsState()

    var passengerName by remember { mutableStateOf(currentUser?.displayName ?: "Haq Baloch") }
    var passengerPhone by remember { mutableStateOf(currentUser?.phone ?: "+92 333 7894521") }
    var isSubmitting by remember { mutableStateOf(false) }

    val fare = route?.fare ?: 2500

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Confirm Booking",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_booking")
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Payable",
                            fontSize = 14.sp,
                            color = ZaTextSecondary
                        )
                        Text(
                            text = "Rs. $fare",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ZaBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            isSubmitting = true
                            viewModel.confirmBooking(
                                passengerName = passengerName,
                                passengerPhone = passengerPhone,
                                onSuccess = {
                                    isSubmitting = false
                                    onBookingSuccess()
                                }
                            )
                        },
                        enabled = !isSubmitting && passengerName.isNotBlank() && passengerPhone.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("button_confirm_and_pay"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm & Generate E-Ticket",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = ZaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Journey Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Trip Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaNavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Departure", fontSize = 11.sp, color = ZaTextSecondary)
                            Text(route?.origin ?: "Kharan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                            Text("${route?.departureTime ?: "08:00 AM"} PKT", fontSize = 12.sp, color = ZaBluePrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = ZaBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Destination", fontSize = 11.sp, color = ZaTextSecondary)
                            Text(route?.destination ?: "Quetta", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                            Text(travelDate, fontSize = 12.sp, color = ZaTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ZaCardBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Bus Service", fontSize = 11.sp, color = ZaTextSecondary)
                            Text("${route?.busName ?: "ZA Express"} (${route?.busCode ?: "ZE"})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Allocated Seat", fontSize = 11.sp, color = ZaTextSecondary)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ZaBluePrimary
                            ) {
                                Text(
                                    text = "Seat ${seatNumber ?: 10}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Passenger Details Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Passenger Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaNavyDark
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Full Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = passengerName,
                        onValueChange = { passengerName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_passenger_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ZaBluePrimary)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Phone Number (Private & Encrypted)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = passengerPhone,
                        onValueChange = { passengerPhone = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_passenger_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = ZaBluePrimary)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = ZaGreenSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Customer phone number is protected & never publicly visible.",
                            fontSize = 11.sp,
                            color = ZaTextSecondary
                        )
                    }
                }
            }

            // Fare Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Payment Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaNavyDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Base Ticket Fare", fontSize = 13.sp, color = ZaTextSecondary)
                        Text("Rs. $fare", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Service & Booking Fee", fontSize = 13.sp, color = ZaTextSecondary)
                        Text("Rs. 0 (Free)", fontSize = 13.sp, color = ZaGreenSuccess, fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(color = ZaCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                        Text("Rs. $fare", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ZaBluePrimary)
                    }
                }
            }
        }
    }
}

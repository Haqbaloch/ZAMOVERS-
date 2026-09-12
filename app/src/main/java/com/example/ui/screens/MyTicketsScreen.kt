package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.security.QrCodeGenerator
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    viewModel: MainViewModel,
    onBookNewTrip: () -> Unit
) {
    val userBookings by viewModel.userBookings.collectAsState()
    val activeTicketForQr by viewModel.activeTicketForQr.collectAsState()
    var selectedTab by remember { mutableStateOf("Upcoming") } // Upcoming, Completed, Cancelled

    val filteredBookings = remember(userBookings, selectedTab) {
        when (selectedTab) {
            "Upcoming" -> userBookings.filter { it.status == "Confirmed" }
            "Completed" -> userBookings.filter { it.status == "Boarded" }
            "Cancelled" -> userBookings.filter { it.status == "Cancelled" }
            else -> userBookings
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Tickets",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZaNavyDark)
            )
        },
        containerColor = ZaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Tab Selector matching reference image 1
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZaNavyDark)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Upcoming", "Completed", "Cancelled").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ZaBluePrimary else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 8.dp)
                                .testTag("tab_${tab.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Ticket list or empty state
            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(ZaBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = ZaBluePrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            text = "No $selectedTab Tickets",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaNavyDark
                        )
                        Text(
                            text = "You don't have any $selectedTab tickets at the moment.",
                            fontSize = 13.sp,
                            color = ZaTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        if (selectedTab == "Upcoming") {
                            Button(
                                onClick = onBookNewTrip,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                            ) {
                                Text("Book a Bus Now")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredBookings) { booking ->
                        TicketCard(
                            booking = booking,
                            onViewQr = { viewModel.activeTicketForQr.value = booking },
                            onCancel = { viewModel.cancelUserBooking(booking.bookingReference) }
                        )
                    }
                }
            }
        }
    }

    // QR E-Ticket Dialog
    if (activeTicketForQr != null) {
        TicketDetailQrDialog(
            booking = activeTicketForQr!!,
            onDismiss = { viewModel.activeTicketForQr.value = null }
        )
    }
}

@Composable
fun TicketCard(
    booking: Booking,
    onViewQr: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_card_${booking.bookingReference}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Bus Icon, Name, Route, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZaBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = booking.busName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaNavyDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = booking.origin,
                                fontSize = 13.sp,
                                color = ZaTextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = ZaBluePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = booking.destination,
                                fontSize = 13.sp,
                                color = ZaTextSecondary
                            )
                        }
                    }
                }

                // Status Badge matching reference image 1
                val (statusBg, statusTextColor) = when (booking.status) {
                    "Confirmed" -> Pair(ZaGreenLight, ZaGreenSuccess)
                    "Boarded" -> Pair(ZaBlueLight, ZaBluePrimary)
                    else -> Pair(ZaRedLight, ZaRedDanger)
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusBg
                ) {
                    Text(
                        text = booking.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = ZaCardBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Details: Date, Departure Time, Seat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = ZaTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.travelDate,
                        fontSize = 12.sp,
                        color = ZaTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ZaTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.departureTimePkt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZaNavyDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AirlineSeatReclineNormal,
                        contentDescription = null,
                        tint = ZaBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Seat ${booking.seatNumber}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaBluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Booking ID and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = ZaTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.bookingReference,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZaTextSecondary
                    )
                }

                Text(
                    text = "Rs. ${booking.fare}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = ZaBluePrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: View QR & Cancel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewQr,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("button_view_qr_${booking.bookingReference}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZaNavyDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View E-Ticket & QR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (booking.status == "Confirmed") {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("button_cancel_${booking.bookingReference}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaRedDanger),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZaRedDanger.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TicketDetailQrDialog(
    booking: Booking,
    onDismiss: () -> Unit
) {
    val qrBitmap = remember(booking.qrSecurityToken) {
        QrCodeGenerator.generateQrBitmap(booking.qrSecurityToken, size = 400)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_qr_ticket"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ZA MOVERS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ZaNavyDark
                        )
                        Text(
                            text = "Official Boarding Pass",
                            fontSize = 11.sp,
                            color = ZaTextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ZaTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High Contrast QR Code
                Surface(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, ZaBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    color = Color.White
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Ticket QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Scan to Board Bus",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZaBluePrimary
                )
                Text(
                    text = booking.bookingReference,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ZaNavyDark
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = ZaCardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // Passenger and Journey Specs
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Passenger", fontSize = 12.sp, color = ZaTextSecondary)
                        Text(booking.passengerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Route", fontSize = 12.sp, color = ZaTextSecondary)
                        Text("${booking.origin} → ${booking.destination}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Seat Number", fontSize = 12.sp, color = ZaTextSecondary)
                        Text("Seat ${booking.seatNumber}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZaBluePrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Departure (PKT)", fontSize = 12.sp, color = ZaTextSecondary)
                        Text(booking.departureTimePkt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status", fontSize = 12.sp, color = ZaTextSecondary)
                        val statusColor = if (booking.status == "Boarded") ZaBluePrimary else ZaGreenSuccess
                        Text(booking.status, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

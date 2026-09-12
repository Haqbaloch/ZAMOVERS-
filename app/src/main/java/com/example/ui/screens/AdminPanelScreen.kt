package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.model.BusRoute
import com.example.data.repository.QrVerificationResult
import com.example.data.repository.ZaMoversRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOwnerAdmin by viewModel.isOwnerAdmin.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()
    val allBookings by viewModel.allAdminBookings.collectAsState()
    val allRoutes by viewModel.allRoutes.collectAsState()
    val securityAlerts by viewModel.securityAlerts.collectAsState()
    val qrScanResult by viewModel.qrScanResult.collectAsState()

    var activeTab by remember { mutableStateOf("Scanner") } // Scanner, Bookings, Routes, Fleet, Security
    var showScannerDialog by remember { mutableStateOf(false) }
    var showAddRouteDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshAdminStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Panel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Official ZA MOVERS Operations Hub",
                            fontSize = 11.sp,
                            color = ZaYellowAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_admin")
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
        containerColor = ZaBackground
    ) { innerPadding ->
        // Server-Side Authorization Check
        if (!isOwnerAdmin || currentUser?.email != ZaMoversRepository.OWNER_EMAIL) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Restricted",
                            tint = ZaRedDanger,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Access Restricted",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This Operations Hub is strictly restricted to the authorized ZA MOVERS Owner account (${ZaMoversRepository.OWNER_EMAIL}).",
                            fontSize = 13.sp,
                            color = ZaTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                        ) {
                            Text("Return to App")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Owner Authentication Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ZaNavyDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ZaYellowAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = ZaNavyDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Verified Owner Session",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = ZaMoversRepository.OWNER_EMAIL,
                                    fontSize = 12.sp,
                                    color = ZaYellowAccent
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ZaGreenSuccess
                            ) {
                                Text(
                                    text = "ONLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Primary Quick Action: "Scan Ticket QR" Button (Step 3-8 in QR prompt)
                item {
                    Button(
                        onClick = { showScannerDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("button_open_qr_scanner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Scan Passenger Ticket QR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Camera scanner • Real-time boarding verification",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Operations Stats Grid
                item {
                    val stats = adminStats
                    val totalRev = stats?.totalRevenue ?: (allBookings.filter { it.status != "Cancelled" }.sumOf { it.fare.toLong() })
                    val totalBk = stats?.totalBookings ?: allBookings.size
                    val boarded = stats?.todayBoarded ?: allBookings.count { it.status == "Boarded" }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminStatCard(
                                title = "Total Bookings",
                                value = "$totalBk",
                                icon = Icons.Default.ConfirmationNumber,
                                color = ZaBluePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Total Revenue",
                                value = "Rs. $totalRev",
                                icon = Icons.Default.Payments,
                                color = ZaGreenSuccess,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminStatCard(
                                title = "Boarded Passengers",
                                value = "$boarded",
                                icon = Icons.Default.HowToReg,
                                color = Color(0xFF9333EA),
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = "Active Fleet",
                                value = "8 Buses",
                                icon = Icons.Default.DirectionsBus,
                                color = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section Tabs: Bookings, Routes, Fleet, Broadcast, Security Email Log
                item {
                    ScrollableTabRow(
                        selectedTabIndex = listOf("Scanner", "Bookings", "Routes", "Fleet", "Security").indexOf(activeTab).coerceAtLeast(0),
                        containerColor = Color.White,
                        contentColor = ZaBluePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        listOf(
                            Pair("Scanner", Icons.Default.QrCodeScanner),
                            Pair("Bookings", Icons.Default.ConfirmationNumber),
                            Pair("Routes", Icons.Default.AltRoute),
                            Pair("Fleet", Icons.Default.DirectionsBus),
                            Pair("Security", Icons.Default.Security)
                        ).forEach { (tab, icon) ->
                            Tab(
                                selected = activeTab == tab,
                                onClick = { activeTab = tab },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(tab, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            )
                        }
                    }
                }

                // Content for Active Tab
                when (activeTab) {
                    "Scanner" -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Text(
                                        text = "Conductor / Terminal Boarding Gate",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Point camera at customer's printed or mobile e-ticket QR code. The system validates the token and marks the passenger as boarded.",
                                        fontSize = 12.sp,
                                        color = ZaTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Quick test chip simulation
                                    Text(
                                        text = "Quick Test Simulated Tickets:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ZaTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.verifyAndBoardTicket("ZA7845293")
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ZaNavyDark),
                                            modifier = Modifier.testTag("test_scan_sample_ticket")
                                        ) {
                                            Text("Scan ZA7845293", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.verifyAndBoardTicket("INVALID_CODE_999")
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B))
                                        ) {
                                            Text("Test Invalid QR", fontSize = 12.sp)
                                        }
                                    }

                                    // Verification result card
                                    if (qrScanResult != null) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        when (val res = qrScanResult!!) {
                                            is QrVerificationResult.Success -> {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(containerColor = ZaGreenLight)
                                                ) {
                                                    Column(modifier = Modifier.padding(14.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ZaGreenSuccess)
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = "Passenger Boarded Successfully!",
                                                                fontWeight = FontWeight.Bold,
                                                                color = ZaGreenSuccess,
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text("Ticket: ${res.booking.bookingReference} | Seat: ${res.booking.seatNumber}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                        Text("Passenger: ${res.booking.passengerName}", fontSize = 12.sp)
                                                        Text("Route: ${res.booking.origin} → ${res.booking.destination} (${res.booking.busName})", fontSize = 12.sp)
                                                        Text("Boarded At: ${res.booking.boardedTimestampPkt}", fontSize = 12.sp, color = ZaGreenSuccess, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }
                                            }
                                            is QrVerificationResult.AlreadyBoarded -> {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(containerColor = ZaRedLight)
                                                ) {
                                                    Column(modifier = Modifier.padding(14.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.Warning, contentDescription = null, tint = ZaRedDanger)
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(
                                                                text = "DUPLICATE BOARDING ATTEMPT",
                                                                fontWeight = FontWeight.Black,
                                                                color = ZaRedDanger,
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(res.reason, fontSize = 12.sp, color = ZaRedDanger, fontWeight = FontWeight.Medium)
                                                        Text("Ticket: ${res.booking.bookingReference} | Passenger: ${res.booking.passengerName}", fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                            is QrVerificationResult.Error -> {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = CardDefaults.cardColors(containerColor = ZaRedLight)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(14.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Error, contentDescription = null, tint = ZaRedDanger)
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(res.message, fontSize = 12.sp, color = ZaRedDanger, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Bookings" -> {
                        items(allBookings) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = booking.bookingReference,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = ZaNavyDark
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = if (booking.status == "Boarded") ZaGreenLight else ZaBlueLight
                                        ) {
                                            Text(
                                                text = booking.status,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (booking.status == "Boarded") ZaGreenSuccess else ZaBluePrimary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Passenger: ${booking.passengerName} (Seat ${booking.seatNumber})", fontSize = 13.sp)
                                    Text("Route: ${booking.origin} → ${booking.destination} | ${booking.travelDate}", fontSize = 12.sp, color = ZaTextSecondary)
                                    Text("Fare: Rs. ${booking.fare}", fontSize = 12.sp, color = ZaBluePrimary, fontWeight = FontWeight.Bold)

                                    if (booking.status == "Confirmed") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                viewModel.verifyAndBoardTicket(booking.bookingReference)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ZaGreenSuccess)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Mark Boarded", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Routes" -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Active Bus Routes", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { showAddRouteDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Route", fontSize = 12.sp)
                                }
                            }
                        }

                        items(allRoutes) { route ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${route.origin} → ${route.destination}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = ZaNavyDark
                                        )
                                        Text(
                                            text = "Rs. ${route.fare}",
                                            fontWeight = FontWeight.Bold,
                                            color = ZaBluePrimary,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${route.busName} (${route.busModel}) • ${route.departureTime} (PKT)", fontSize = 12.sp, color = ZaTextSecondary)
                                    Text("Duration: ${route.duration} • 44 Seats • AC: ${if (route.isAc) "Yes" else "No"}", fontSize = 12.sp, color = ZaTextSecondary)
                                }
                            }
                        }
                    }

                    "Fleet" -> {
                        item {
                            Text("ZA MOVERS Bus Fleet", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        items(
                            listOf(
                                Triple("ZA Express (ZE-01)", "Yutong Master 2026", "Active • Route: Kharan <-> Quetta"),
                                Triple("Baloch Coach (BC-02)", "Daewoo Express 2025", "Active • Route: Kharan <-> Quetta"),
                                Triple("Quetta Lines (QL-03)", "Yutong Nova Luxury", "Active • Route: Kharan <-> Quetta"),
                                Triple("Kharan Super (KS-04)", "Higer Luxury Coach", "Active • Route: Kharan <-> Quetta"),
                                Triple("ZA Royal Class (ZR-05)", "Yutong Master Sleeper", "Active • Route: Quetta <-> Karachi"),
                                Triple("Chagai Express (CE-06)", "Daewoo Express", "Active • Route: Quetta <-> Dalbandin")
                            )
                        ) { (bus, model, details) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ZaBluePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(bus, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(model, fontSize = 12.sp, color = ZaTextSecondary)
                                        Text(details, fontSize = 11.sp, color = ZaGreenSuccess)
                                    }
                                    Surface(shape = RoundedCornerShape(50), color = ZaGreenLight) {
                                        Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ZaGreenSuccess, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }

                    "Security" -> {
                        item {
                            Text(
                                text = "Login Security Alert Emails Audit Log (Step 8)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaNavyDark
                            )
                            Text(
                                text = "Every Google Sign-In automatically dispatches a detailed security alert to the user's Gmail address.",
                                fontSize = 12.sp,
                                color = ZaTextSecondary
                            )
                        }

                        items(securityAlerts) { alert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(alert.userEmail, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ZaNavyDark)
                                        Surface(shape = RoundedCornerShape(50), color = ZaGreenLight) {
                                            Text(
                                                text = "DELIVERED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ZaGreenSuccess,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Time (PKT): ${alert.loginDatePkt} at ${alert.loginTimePkt}", fontSize = 12.sp, color = ZaTextSecondary)
                                    Text("Device: ${alert.deviceName} • ${alert.osVersion}", fontSize = 12.sp, color = ZaTextSecondary)
                                    Text("Location: ${alert.city}, ${alert.region}, ${alert.country} (IP: ${alert.ipAddress})", fontSize = 12.sp, color = ZaBluePrimary)
                                    Text("Status: ${alert.emailStatus}", fontSize = 11.sp, color = ZaGreenSuccess, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Live QR Scanner Dialog
    if (showScannerDialog) {
        AdminQrScannerDialog(
            onDismiss = {
                showScannerDialog = false
                viewModel.clearQrScanResult()
            },
            onScanToken = { token ->
                viewModel.verifyAndBoardTicket(token)
            },
            scanResult = qrScanResult
        )
    }

    // Modal Add Route Dialog
    if (showAddRouteDialog) {
        AddRouteDialog(
            onDismiss = { showAddRouteDialog = false },
            onAdd = { origin, dest, busName, code, model, depTime, duration, fare, isAc ->
                viewModel.addNewRoute(origin, dest, busName, code, model, depTime, duration, fare, isAc)
                showAddRouteDialog = false
            }
        )
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 11.sp, color = ZaTextSecondary, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
        }
    }
}

@Composable
fun AdminQrScannerDialog(
    onDismiss: () -> Unit,
    onScanToken: (String) -> Unit,
    scanResult: QrVerificationResult?
) {
    var manualInput by remember { mutableStateOf("") }

    // Laser scanning bar animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_anim"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_admin_qr_scanner"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ticket QR Scanner",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaNavyDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Viewfinder Box with Laser Line
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZaNavyDark)
                        .border(2.dp, ZaBluePrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Viewfinder corner marks
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(160.dp)
                    )

                    // Laser line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (laserOffset - 100).dp)
                            .height(2.dp)
                            .background(ZaRedDanger)
                    )

                    Text(
                        text = "Align QR Code inside frame",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Manual input or test token
                OutlinedTextField(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    placeholder = { Text("Enter Ticket ID or QR Token", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (manualInput.isNotBlank()) onScanToken(manualInput)
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Scan", tint = ZaBluePrimary)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onScanToken("ZA7845293") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                    ) {
                        Text("Scan ZA7845293", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { onScanToken("INVALID_QR_TOKEN") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Test Bad Token", fontSize = 11.sp)
                    }
                }

                // Scan result preview
                if (scanResult != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    when (scanResult) {
                        is QrVerificationResult.Success -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ZaGreenLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("✓ BOARDING VERIFIED", fontWeight = FontWeight.Black, color = ZaGreenSuccess, fontSize = 13.sp)
                                    Text("Ticket: ${scanResult.booking.bookingReference} | Seat ${scanResult.booking.seatNumber}", fontSize = 12.sp)
                                    Text("Passenger: ${scanResult.booking.passengerName}", fontSize = 12.sp)
                                    Text("Boarded At: ${scanResult.booking.boardedTimestampPkt}", fontSize = 11.sp, color = ZaGreenSuccess)
                                }
                            }
                        }
                        is QrVerificationResult.AlreadyBoarded -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ZaRedLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("⚠ DUPLICATE BOARDING ATTEMPT", fontWeight = FontWeight.Black, color = ZaRedDanger, fontSize = 13.sp)
                                    Text(scanResult.reason, fontSize = 11.sp, color = ZaRedDanger)
                                }
                            }
                        }
                        is QrVerificationResult.Error -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ZaRedLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(scanResult.message, color = ZaRedDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddRouteDialog(
    onDismiss: () -> Unit,
    onAdd: (origin: String, dest: String, busName: String, code: String, model: String, depTime: String, duration: String, fare: Int, isAc: Boolean) -> Unit
) {
    var origin by remember { mutableStateOf("Kharan") }
    var dest by remember { mutableStateOf("Quetta") }
    var busName by remember { mutableStateOf("ZA Executive") }
    var busCode by remember { mutableStateOf("ZX") }
    var busModel by remember { mutableStateOf("Yutong Luxury 2026") }
    var depTime by remember { mutableStateOf("11:30 AM") }
    var duration by remember { mutableStateOf("6h 30m") }
    var fareStr by remember { mutableStateOf("2600") }
    var isAc by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Add New Route & Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ZaNavyDark)
                OutlinedTextField(value = origin, onValueChange = { origin = it }, label = { Text("Origin City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dest, onValueChange = { dest = it }, label = { Text("Destination City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = busName, onValueChange = { busName = it }, label = { Text("Bus Name") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = busCode, onValueChange = { busCode = it }, label = { Text("Code") }, singleLine = true, modifier = Modifier.width(80.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = depTime, onValueChange = { depTime = it }, label = { Text("Time (PKT)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fareStr, onValueChange = { fareStr = it }, label = { Text("Fare (PKR)") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAc, onCheckedChange = { isAc = it })
                    Text("Air Conditioned (AC)", fontSize = 13.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val fare = fareStr.toIntOrNull() ?: 2500
                            onAdd(origin, dest, busName, busCode, busModel, depTime, duration, fare, isAc)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                    ) {
                        Text("Save Route")
                    }
                }
            }
        }
    }
}

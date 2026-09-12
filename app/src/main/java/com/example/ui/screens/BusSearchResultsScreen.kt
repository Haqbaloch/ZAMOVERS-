package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.BusRoute
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusSearchResultsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSelectRoute: (BusRoute) -> Unit
) {
    val departureCity by viewModel.departureCity.collectAsState()
    val destinationCity by viewModel.destinationCity.collectAsState()
    val travelDate by viewModel.travelDate.collectAsState()
    val allRoutes by viewModel.allRoutes.collectAsState()

    // Filter routes matching origin and destination
    val matchingRoutes = remember(allRoutes, departureCity, destinationCity) {
        val filtered = allRoutes.filter {
            it.origin.equals(departureCity, ignoreCase = true) &&
            it.destination.equals(destinationCity, ignoreCase = true)
        }
        if (filtered.isNotEmpty()) filtered
        else {
            // If user searched a new pair without seed data, dynamically generate available luxury coaches
            listOf(
                BusRoute(
                    origin = departureCity,
                    destination = destinationCity,
                    busName = "ZA Express",
                    busCode = "ZE",
                    busModel = "Yutong Master",
                    departureTime = "08:00 AM",
                    duration = "6h 30m",
                    fare = 2500,
                    isAc = true
                ),
                BusRoute(
                    origin = departureCity,
                    destination = destinationCity,
                    busName = "Baloch Coach",
                    busCode = "BC",
                    busModel = "Daewoo",
                    departureTime = "10:00 AM",
                    duration = "6h 45m",
                    fare = 2400,
                    isAc = false
                ),
                BusRoute(
                    origin = departureCity,
                    destination = destinationCity,
                    busName = "Quetta Lines",
                    busCode = "QL",
                    busModel = "Yutong Nova",
                    departureTime = "02:00 PM",
                    duration = "6h 30m",
                    fare = 2400,
                    isAc = true
                ),
                BusRoute(
                    origin = departureCity,
                    destination = destinationCity,
                    busName = "Kharan Super",
                    busCode = "KS",
                    busModel = "Higer Luxury",
                    departureTime = "09:00 PM",
                    duration = "6h 15m",
                    fare = 2600,
                    isAc = true
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = departureCity,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = destinationCity,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = travelDate,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_search")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Chips row: "X buses found", "1 passenger(s)" matching reference image 4
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ZaNavyDark.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${matchingRoutes.size} buses found",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ZaNavyDark.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1 passenger(s)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // List of bus cards
            items(matchingRoutes) { route ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bus_card_${route.busCode.lowercase()}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Top row with Bus Icon Badge, Bus Name, and Departure Time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(ZaBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBus,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = route.busCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = route.busName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaNavyDark
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = route.busModel,
                                            fontSize = 12.sp,
                                            color = ZaTextSecondary
                                        )
                                        if (route.isAc) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = ZaBlueLight
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AcUnit,
                                                        contentDescription = "AC",
                                                        tint = ZaBluePrimary,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = "AC",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ZaBluePrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Departure Time
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = route.departureTime,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZaNavyDark
                                )
                                Text(
                                    text = "Departure",
                                    fontSize = 11.sp,
                                    color = ZaTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Duration and Seats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = ZaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = route.duration,
                                        fontSize = 12.sp,
                                        color = ZaTextSecondary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AirlineSeatReclineNormal,
                                        contentDescription = null,
                                        tint = ZaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${route.totalSeats} Seats",
                                        fontSize = 12.sp,
                                        color = ZaTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = ZaCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Price and Select Button row matching reference image 4
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Per Seat",
                                    fontSize = 11.sp,
                                    color = ZaTextSecondary
                                )
                                Text(
                                    text = "Rs. ${route.fare}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ZaBluePrimary
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.selectRoute(route)
                                    onSelectRoute(route)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                                modifier = Modifier.testTag("select_bus_${route.busCode.lowercase()}")
                            ) {
                                Text(
                                    text = "Select",
                                    fontSize = 14.sp,
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
                }
            }
        }
    }
}

package com.example.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToResults: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    val departureCity by viewModel.departureCity.collectAsState()
    val destinationCity by viewModel.destinationCity.collectAsState()
    val travelDate by viewModel.travelDate.collectAsState()
    val unreadCount by viewModel.unreadNotifCount.collectAsState()

    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLocating by remember { mutableStateOf(false) }

    // Geolocation permission launcher (Step 2 of prompt)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            isLocating = true
            detectCurrentCity(context) { city ->
                isLocating = false
                if (city != null) {
                    viewModel.setDepartureFromGps(city)
                } else {
                    // Fallback to primary Balochistan hub if emulator has no fixed coords
                    viewModel.setDepartureFromGps("Quetta")
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied. Please select city manually.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            // Dark Navy Header matching reference image 1 & 2
            Surface(
                color = ZaNavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ZaBluePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = "ZA Movers Logo",
                                tint = ZaBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ZA MOVERS",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Travel • Connect • Explore",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    // Notification bell with red counter badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onNavigateToNotifications() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(ZaRedDanger),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner matching reference image 1 & 2
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.bus_hero_banner_1789208991013),
                            contentDescription = "ZA Movers Coach",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient overlay for contrast
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            ZaNavyDark.copy(alpha = 0.4f),
                                            ZaNavyDark.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = ZaBluePrimary,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Text(
                                    text = "Premium Bus Travel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Your Journey,\nOur Priority.",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    lineHeight = 26.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Safe • Comfortable • Affordable",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ZaYellowAccent
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Search Card (Steps 1 to 10)
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
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "Book Your Route",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaNavyDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 1: "From" Departure City Input with GPS icon & search icon
                        Text(
                            text = "From",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = departureCity,
                                onValueChange = {
                                    viewModel.departureCity.value = it
                                    showFromDropdown = true
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_departure_city"),
                                placeholder = {
                                    Text(
                                        text = "Select Departure City",
                                        fontSize = 14.sp,
                                        color = ZaTextMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search Departure",
                                        tint = ZaBluePrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        },
                                        modifier = Modifier.testTag("button_gps_detect")
                                    ) {
                                        if (isLocating) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = ZaBluePrimary
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.MyLocation,
                                                contentDescription = "Detect My Location",
                                                tint = ZaBluePrimary
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ZaBluePrimary,
                                    unfocusedBorderColor = ZaCardBorder,
                                    focusedContainerColor = ZaBlueLight.copy(alpha = 0.2f),
                                    unfocusedContainerColor = Color(0xFFF9FAFC)
                                )
                            )

                            // Step 3: Autocomplete dropdown suggestions
                            DropdownMenu(
                                expanded = showFromDropdown,
                                onDismissRequest = { showFromDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                val filtered = viewModel.pakistaniCities.filter {
                                    it.contains(departureCity, ignoreCase = true)
                                }
                                (if (filtered.isEmpty()) viewModel.pakistaniCities else filtered).forEach { city ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationCity,
                                                    contentDescription = null,
                                                    tint = ZaBluePrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(city, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            viewModel.departureCity.value = city
                                            showFromDropdown = false
                                            errorMessage = null
                                        }
                                    )
                                }
                            }
                        }

                        // Step 5: Swap button (⇄) in center
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = ZaCardBorder
                            )
                            Surface(
                                shape = CircleShape,
                                color = ZaBluePrimary,
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        viewModel.swapCities()
                                        errorMessage = null
                                    }
                                    .testTag("button_swap_cities")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Swap Cities",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Step 4: "To" Destination City Input
                        Text(
                            text = "To",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = destinationCity,
                                onValueChange = {
                                    viewModel.destinationCity.value = it
                                    showToDropdown = true
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_destination_city"),
                                placeholder = {
                                    Text(
                                        text = "Select Destination",
                                        fontSize = 14.sp,
                                        color = ZaTextMuted
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = "Destination",
                                        tint = ZaBluePrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showToDropdown = !showToDropdown }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Show cities",
                                            tint = ZaTextSecondary
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ZaBluePrimary,
                                    unfocusedBorderColor = ZaCardBorder,
                                    focusedContainerColor = ZaBlueLight.copy(alpha = 0.2f),
                                    unfocusedContainerColor = Color(0xFFF9FAFC)
                                )
                            )

                            DropdownMenu(
                                expanded = showToDropdown,
                                onDismissRequest = { showToDropdown = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                val filtered = viewModel.pakistaniCities.filter {
                                    it.contains(destinationCity, ignoreCase = true)
                                }
                                (if (filtered.isEmpty()) viewModel.pakistaniCities else filtered).forEach { city ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Place,
                                                    contentDescription = null,
                                                    tint = ZaBluePrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(city, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            viewModel.destinationCity.value = city
                                            showToDropdown = false
                                            errorMessage = null
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Travel Date Row (in Pakistan Standard Time PKT)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9FAFC))
                                .border(1.dp, ZaCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Date",
                                    tint = ZaBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Travel Date (PKT)",
                                        fontSize = 11.sp,
                                        color = ZaTextSecondary
                                    )
                                    Text(
                                        text = travelDate,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaNavyDark
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ZaBlueLight
                            ) {
                                Text(
                                    text = "PKT UTC+5",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZaBluePrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Validation error message (Step 6)
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = errorMessage!!,
                                color = ZaRedDanger,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Search Buses Button (Step 7)
                        Button(
                            onClick = {
                                val from = departureCity.trim()
                                val to = destinationCity.trim()

                                if (from.isBlank() || to.isBlank()) {
                                    errorMessage = "Please enter both Departure and Destination cities."
                                    return@Button
                                }
                                if (from.equals(to, ignoreCase = true)) {
                                    errorMessage = "Departure and Destination cannot be the same city."
                                    return@Button
                                }
                                errorMessage = null
                                onNavigateToResults()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("button_search_buses"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ZaBluePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Search Buses",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Our Services Section matching reference image 1 & 2
            item {
                Text(
                    text = "Our Services",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaNavyDark
                )
                Text(
                    text = "Select a service to get started",
                    fontSize = 12.sp,
                    color = ZaTextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Service 1: Bus Ticket
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToResults() }
                        .testTag("service_bus_ticket"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(ZaBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Bus Ticket",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Bus Ticket",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZaNavyDark
                                )
                                Text(
                                    text = "Book Your Bus Ticket Easily & Safely",
                                    fontSize = 12.sp,
                                    color = ZaTextSecondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ZaBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = ZaBluePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Service 2: Car Reservation (Coming Soon)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = "Car Reservation",
                                    tint = ZaTextSecondary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Car Reservation",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZaNavyDark
                                )
                                Text(
                                    text = "Comfortable Rides Across Pakistan",
                                    fontSize = 12.sp,
                                    color = ZaTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFE2E8F0)
                                ) {
                                    Text(
                                        text = "COMING SOON",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaTextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ZaTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Service 3: Flight Booking (Coming Soon)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFAF5FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flight,
                                    contentDescription = "Flight Booking",
                                    tint = Color(0xFF9333EA),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Flight Booking",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZaNavyDark
                                )
                                Text(
                                    text = "Fly to Your Destination",
                                    fontSize = 12.sp,
                                    color = ZaTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color(0xFFE2E8F0)
                                ) {
                                    Text(
                                        text = "COMING SOON",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaTextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ZaTextMuted
                        )
                    }
                }
            }

            // Bottom Brand Footer
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = ZaBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pakistan's Trusted Travel Partner",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZaTextSecondary
                    )
                }
            }
        }
    }
}

// Helper to detect current city via Android LocationManager + Geocoder
@SuppressLint("MissingPermission")
private fun detectCurrentCity(context: Context, onResult: (String?) -> Unit) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null && Geocoder.isPresent()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val city = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
            if (!city.isNullOrBlank()) {
                onResult(city)
                return
            }
        }
        // Fallback default
        onResult("Quetta")
    } catch (e: Exception) {
        onResult("Quetta")
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateToMyTickets: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOwnerAdmin by viewModel.isOwnerAdmin.collectAsState()

    val name = currentUser?.displayName ?: "Haq Baloch"
    val email = currentUser?.email ?: "balochistanalert331@gmail.com"
    val initials = currentUser?.avatarInitials ?: "HB"
    val totalTrips = currentUser?.totalTrips ?: 12
    val upcomingTrips = currentUser?.upcomingTrips ?: 1
    val rating = currentUser?.rating ?: 4.9

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Navy Header with Avatar matching reference image 3
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ZaNavyDark, Color(0xFF0F172A))
                        )
                    )
                    .statusBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar Circle with Camera badge matching reference image 3
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(ZaBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(ZaBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    if (isOwnerAdmin) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ZaYellowAccent.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZaYellowAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ZaYellowAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AUTHORIZED OWNER & ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZaYellowAccent
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Card matching reference image 3 (Trips, Upcoming, Rating)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalTrips",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaBluePrimary
                        )
                        Text(
                            text = "Total Trips",
                            fontSize = 12.sp,
                            color = ZaTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(ZaCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$upcomingTrips",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaGreenSuccess
                        )
                        Text(
                            text = "Upcoming",
                            fontSize = 12.sp,
                            color = ZaTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(ZaCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$rating",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Rating",
                            fontSize = 12.sp,
                            color = ZaTextSecondary
                        )
                    }
                }
            }
        }

        // Section: My Account
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "My Account",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaNavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        // Admin Panel Row (Prominent if Owner)
                        if (isOwnerAdmin) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(ZaBlueLight.copy(alpha = 0.35f))
                                    .clickable { onNavigateToAdminPanel() }
                                    .padding(16.dp)
                                    .testTag("button_open_admin_panel"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ZaNavyDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = "Admin Panel",
                                            tint = ZaYellowAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Admin Panel",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ZaNavyDark
                                        )
                                        Text(
                                            text = "Scan QR, fleet, bookings, routes",
                                            fontSize = 11.sp,
                                            color = ZaTextSecondary
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = ZaNavyDark
                                ) {
                                    Text(
                                        text = "OWNER ONLY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaYellowAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = ZaCardBorder)
                        }

                        ProfileMenuRow(
                            icon = Icons.Default.ConfirmationNumber,
                            title = "My Tickets",
                            subtitle = "View current and past booked tickets",
                            onClick = onNavigateToMyTickets,
                            tag = "menu_my_tickets"
                        )
                        HorizontalDivider(color = ZaCardBorder)
                        ProfileMenuRow(
                            icon = Icons.Default.History,
                            title = "Booking History",
                            subtitle = "12 completed trips across Balochistan",
                            onClick = onNavigateToMyTickets,
                            tag = "menu_booking_history"
                        )
                        HorizontalDivider(color = ZaCardBorder)
                        ProfileMenuRow(
                            icon = Icons.Default.Person,
                            title = "Account Settings",
                            subtitle = "Google authenticated profile details",
                            onClick = {},
                            tag = "menu_account_settings"
                        )
                    }
                }
            }
        }

        // Section: Support & Google Authentication
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Security & Support",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaNavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ProfileMenuRow(
                            icon = Icons.Default.HelpOutline,
                            title = "Help Center & Contact",
                            subtitle = "24/7 passenger assistance",
                            onClick = {},
                            tag = "menu_help"
                        )
                        HorizontalDivider(color = ZaCardBorder)
                        ProfileMenuRow(
                            icon = Icons.Default.Security,
                            title = "Security & Privacy",
                            subtitle = "Protected passenger phone & data",
                            onClick = {},
                            tag = "menu_security"
                        )
                        HorizontalDivider(color = ZaCardBorder)
                        ProfileMenuRow(
                            icon = Icons.Default.SwitchAccount,
                            title = "Switch Google Account",
                            subtitle = "Currently signed in as $email",
                            onClick = onNavigateToSignIn,
                            tag = "menu_switch_account"
                        )
                        HorizontalDivider(color = ZaCardBorder)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.signOut()
                                    onNavigateToSignIn()
                                }
                                .padding(16.dp)
                                .testTag("menu_logout"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ZaRedLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Log Out",
                                    tint = ZaRedDanger,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sign Out",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaRedDanger
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ZaBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ZaBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZaNavyDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = ZaTextSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = ZaTextMuted
        )
    }
}

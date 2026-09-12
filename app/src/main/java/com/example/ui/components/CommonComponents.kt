package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class AppNavDestination {
    HOME,
    MY_TICKETS,
    NOTIFICATIONS,
    PROFILE,
    SEARCH_RESULTS,
    SEAT_SELECTION,
    PASSENGER_BOOKING,
    ADMIN_PANEL,
    SIGN_IN
}

@Composable
fun ZaBottomNavigationBar(
    currentDestination: AppNavDestination,
    unreadCount: Int,
    onNavigate: (AppNavDestination) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 16.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZaBottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentDestination == AppNavDestination.HOME,
                onClick = { onNavigate(AppNavDestination.HOME) }
            )
            ZaBottomNavItem(
                icon = Icons.Default.ConfirmationNumber,
                label = "My Tickets",
                isSelected = currentDestination == AppNavDestination.MY_TICKETS,
                onClick = { onNavigate(AppNavDestination.MY_TICKETS) }
            )
            ZaBottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                badgeCount = unreadCount,
                isSelected = currentDestination == AppNavDestination.NOTIFICATIONS,
                onClick = { onNavigate(AppNavDestination.NOTIFICATIONS) }
            )
            ZaBottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = currentDestination == AppNavDestination.PROFILE,
                onClick = { onNavigate(AppNavDestination.PROFILE) }
            )
        }
    }
}

@Composable
fun ZaBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag("nav_item_${label.lowercase().replace(" ", "_")}")
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ZaBluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = ZaTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(ZaRedDanger),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ZaBluePrimary else ZaTextMuted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZaTopAppBar(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ZaNavyDark,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

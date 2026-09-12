package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppNavDestination
import com.example.ui.components.ZaBottomNavigationBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ZaMoversApp()
            }
        }
    }
}

@Composable
fun ZaMoversApp(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppNavDestination.HOME) }
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // Handle back button logically
    BackHandler(enabled = currentDestination != AppNavDestination.HOME) {
        currentDestination = when (currentDestination) {
            AppNavDestination.SEARCH_RESULTS -> AppNavDestination.HOME
            AppNavDestination.SEAT_SELECTION -> AppNavDestination.SEARCH_RESULTS
            AppNavDestination.PASSENGER_BOOKING -> AppNavDestination.SEAT_SELECTION
            AppNavDestination.ADMIN_PANEL -> AppNavDestination.PROFILE
            AppNavDestination.SIGN_IN -> AppNavDestination.PROFILE
            else -> AppNavDestination.HOME
        }
    }

    val showBottomBar = currentDestination in listOf(
        AppNavDestination.HOME,
        AppNavDestination.MY_TICKETS,
        AppNavDestination.NOTIFICATIONS,
        AppNavDestination.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                ZaBottomNavigationBar(
                    currentDestination = currentDestination,
                    unreadCount = unreadNotifCount,
                    onNavigate = { dest -> currentDestination = dest }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Modifier.padding(innerPadding) // used by inner composables with Scaffold

        when (currentDestination) {
            AppNavDestination.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToResults = {
                        currentDestination = AppNavDestination.SEARCH_RESULTS
                    },
                    onNavigateToNotifications = {
                        currentDestination = AppNavDestination.NOTIFICATIONS
                    }
                )
            }
            AppNavDestination.SEARCH_RESULTS -> {
                BusSearchResultsScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppNavDestination.HOME },
                    onSelectRoute = {
                        currentDestination = AppNavDestination.SEAT_SELECTION
                    }
                )
            }
            AppNavDestination.SEAT_SELECTION -> {
                SeatSelectionScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppNavDestination.SEARCH_RESULTS },
                    onContinueToPassengerDetails = {
                        currentDestination = AppNavDestination.PASSENGER_BOOKING
                    }
                )
            }
            AppNavDestination.PASSENGER_BOOKING -> {
                PassengerBookingScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppNavDestination.SEAT_SELECTION },
                    onBookingSuccess = {
                        currentDestination = AppNavDestination.MY_TICKETS
                    }
                )
            }
            AppNavDestination.MY_TICKETS -> {
                MyTicketsScreen(
                    viewModel = viewModel,
                    onBookNewTrip = { currentDestination = AppNavDestination.HOME }
                )
            }
            AppNavDestination.NOTIFICATIONS -> {
                NotificationsScreen(
                    viewModel = viewModel
                )
            }
            AppNavDestination.PROFILE -> {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToMyTickets = { currentDestination = AppNavDestination.MY_TICKETS },
                    onNavigateToAdminPanel = { currentDestination = AppNavDestination.ADMIN_PANEL },
                    onNavigateToSignIn = { currentDestination = AppNavDestination.SIGN_IN }
                )
            }
            AppNavDestination.ADMIN_PANEL -> {
                AdminPanelScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppNavDestination.PROFILE }
                )
            }
            AppNavDestination.SIGN_IN -> {
                SignInScreen(
                    viewModel = viewModel,
                    onSignInSuccess = { currentDestination = AppNavDestination.HOME }
                )
            }
        }
    }
}

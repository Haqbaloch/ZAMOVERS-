package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ZaMoversDatabase
import com.example.data.model.*
import com.example.data.repository.QrVerificationResult
import com.example.data.repository.ZaMoversRepository
import com.example.data.security.TimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ZaMoversDatabase.getDatabase(application, viewModelScope)
    private val repository = ZaMoversRepository(database)

    // Pakistani cities for departure & destination autocomplete
    val pakistaniCities = listOf(
        "Kharan",
        "Quetta",
        "Karachi",
        "Dalbandin",
        "Gwadar",
        "Turbat",
        "Kalat",
        "Mastung",
        "Hub",
        "Sibi",
        "Jacobabad",
        "Islamabad",
        "Lahore"
    )

    // Current user state (Default initialized to owner as logged in, or ready for Google Sign In)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isOwnerAdmin = MutableStateFlow(false)
    val isOwnerAdmin: StateFlow<Boolean> = _isOwnerAdmin.asStateFlow()

    private val _lastSecurityAlert = MutableStateFlow<SecurityAlert?>(null)
    val lastSecurityAlert: StateFlow<SecurityAlert?> = _lastSecurityAlert.asStateFlow()

    // Search state
    val departureCity = MutableStateFlow("Kharan")
    val destinationCity = MutableStateFlow("Quetta")
    val travelDate = MutableStateFlow("Sun, 13 Sep 2026")
    val passengersCount = MutableStateFlow(1)

    // Active Routes
    val allRoutes: StateFlow<List<BusRoute>> = repository.getAllRoutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Route for booking
    val selectedRoute = MutableStateFlow<BusRoute?>(null)

    // Selected Seat
    val selectedSeat = MutableStateFlow<Int?>(null)

    // Booked seats for current route & date
    val bookedSeats: StateFlow<List<Int>> = combine(
        selectedRoute,
        travelDate
    ) { route, date ->
        Pair(route, date)
    }.flatMapLatest { (route, date) ->
        if (route != null) {
            repository.getBookedSeats(route.origin, route.destination, route.busName, date)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Bookings
    val userBookings: StateFlow<List<Booking>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserBookings(user.email)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.getUnreadNotificationsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    // Admin State
    val adminStats = MutableStateFlow<AdminDashboardStats?>(null)

    val allAdminBookings: StateFlow<List<Booking>> = repository.getAllBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val securityAlerts: StateFlow<List<SecurityAlert>> = repository.getSecurityAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val qrScanResult = MutableStateFlow<QrVerificationResult?>(null)

    // Selected ticket for QR dialog view
    val activeTicketForQr = MutableStateFlow<Booking?>(null)

    // UI Message Toast/Banner
    val uiMessage = MutableStateFlow<String?>(null)

    init {
        // Automatically sign in default authorized owner account so user can immediately test
        signInWithGoogle(ZaMoversRepository.OWNER_EMAIL, "Haq Baloch")
    }

    fun signInWithGoogle(email: String, displayName: String) {
        viewModelScope.launch {
            val (user, alert) = repository.authenticateWithGoogle(email, displayName)
            _currentUser.value = user
            _lastSecurityAlert.value = alert
            _isOwnerAdmin.value = repository.verifyAdminAccess(user.email)
            refreshAdminStats()
        }
    }

    fun signOut() {
        _currentUser.value = null
        _isOwnerAdmin.value = false
        _lastSecurityAlert.value = null
    }

    fun swapCities() {
        val temp = departureCity.value
        departureCity.value = destinationCity.value
        destinationCity.value = temp
    }

    fun setDepartureFromGps(detectedCity: String) {
        departureCity.value = detectedCity
        uiMessage.value = "Departure set to $detectedCity (GPS Detected)"
    }

    fun selectRoute(route: BusRoute) {
        selectedRoute.value = route
        selectedSeat.value = null
    }

    fun selectSeatNumber(seat: Int) {
        if (bookedSeats.value.contains(seat)) return
        selectedSeat.value = seat
    }

    fun confirmBooking(
        passengerName: String,
        passengerPhone: String,
        onSuccess: (Booking) -> Unit
    ) {
        val route = selectedRoute.value ?: return
        val seat = selectedSeat.value ?: return
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val booking = repository.createBooking(
                userEmail = user.email,
                passengerName = passengerName.ifBlank { user.displayName },
                passengerPhone = passengerPhone.ifBlank { user.phone },
                origin = route.origin,
                destination = route.destination,
                busName = route.busName,
                busCode = route.busCode,
                seatNumber = seat,
                fare = route.fare,
                travelDate = travelDate.value,
                departureTime = route.departureTime
            )
            activeTicketForQr.value = booking
            refreshAdminStats()
            onSuccess(booking)
        }
    }

    fun verifyAndBoardTicket(scannedTokenOrRef: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.verifyAndBoardTicket(user.email, scannedTokenOrRef)
            qrScanResult.value = result
            refreshAdminStats()
        }
    }

    fun clearQrScanResult() {
        qrScanResult.value = null
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun cancelUserBooking(reference: String) {
        viewModelScope.launch {
            repository.cancelBooking(reference)
            refreshAdminStats()
            uiMessage.value = "Booking $reference cancelled successfully"
        }
    }

    fun addNewRoute(
        origin: String,
        destination: String,
        busName: String,
        busCode: String,
        busModel: String,
        departureTime: String,
        duration: String,
        fare: Int,
        isAc: Boolean
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val route = BusRoute(
                origin = origin,
                destination = destination,
                busName = busName,
                busCode = busCode,
                busModel = busModel,
                departureTime = departureTime,
                duration = duration,
                fare = fare,
                isAc = isAc
            )
            repository.addRoute(user.email, route)
            refreshAdminStats()
            uiMessage.value = "New route $origin → $destination added"
        }
    }

    fun broadcastAdminNotification(title: String, message: String, category: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.broadcastNotification(user.email, title, message, category)
            uiMessage.value = "Notification broadcasted to all passengers"
        }
    }

    fun refreshAdminStats() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            adminStats.value = repository.getAdminStats(user.email)
        }
    }

    fun clearMessage() {
        uiMessage.value = null
    }
}

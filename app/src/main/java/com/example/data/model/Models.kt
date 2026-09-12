package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val displayName: String,
    val role: String = "CUSTOMER", // "OWNER_ADMIN" or "CUSTOMER"
    val avatarInitials: String = "HB",
    val phone: String = "+92 300 1234567",
    val totalTrips: Int = 12,
    val upcomingTrips: Int = 1,
    val rating: Double = 4.9
)

@Entity(tableName = "routes")
data class BusRoute(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val origin: String,
    val destination: String,
    val busName: String,
    val busCode: String,
    val busModel: String,
    val departureTime: String, // e.g. "08:00 AM" (PKT)
    val duration: String, // e.g. "6h 30m"
    val fare: Int, // e.g. 2500 PKR
    val totalSeats: Int = 44,
    val isAc: Boolean = true,
    val isActive: Boolean = true
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingReference: String, // e.g. "ZA7845293"
    val userEmail: String,
    val passengerName: String,
    val passengerPhone: String,
    val origin: String,
    val destination: String,
    val busName: String,
    val busCode: String,
    val seatNumber: Int,
    val fare: Int,
    val travelDate: String, // e.g. "Wed, 10 Sep 2026"
    val departureTimePkt: String, // e.g. "08:00 AM PKT"
    val bookingTimestampPkt: String, // accurate PKT timestamp
    val status: String = "Confirmed", // "Confirmed", "Boarded", "Cancelled"
    val qrSecurityToken: String,
    val boardedTimestampPkt: String? = null
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timeAgo: String,
    val category: String, // "booking", "ticket", "schedule", "notice", "offer"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "security_alerts")
data class SecurityAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val loginDatePkt: String,
    val loginTimePkt: String,
    val deviceName: String,
    val osVersion: String,
    val appVersion: String,
    val ipAddress: String,
    val city: String,
    val region: String,
    val country: String,
    val emailStatus: String = "Delivered to Gmail",
    val sentTimestamp: Long = System.currentTimeMillis()
)

data class AdminDashboardStats(
    val totalBookings: Int,
    val totalRevenue: Long,
    val activeBuses: Int,
    val totalRoutes: Int,
    val todayBoarded: Int,
    val upcomingTrips: Int
)

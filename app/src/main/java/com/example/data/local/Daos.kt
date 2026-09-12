package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes WHERE isActive = 1")
    fun getAllActiveRoutes(): Flow<List<BusRoute>>

    @Query("SELECT * FROM routes WHERE origin = :origin AND destination = :destination AND isActive = 1")
    fun searchRoutes(origin: String, destination: String): Flow<List<BusRoute>>

    @Query("SELECT * FROM routes WHERE id = :id LIMIT 1")
    suspend fun getRouteById(id: Long): BusRoute?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: BusRoute): Long

    @Update
    suspend fun updateRoute(route: BusRoute)

    @Delete
    suspend fun deleteRoute(route: BusRoute)

    @Query("SELECT COUNT(*) FROM routes")
    suspend fun getRouteCount(): Int
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE userEmail = :email ORDER BY id DESC")
    fun getBookingsForUser(email: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE bookingReference = :reference LIMIT 1")
    suspend fun getBookingByReference(reference: String): Booking?

    @Query("SELECT * FROM bookings WHERE qrSecurityToken = :token LIMIT 1")
    suspend fun getBookingByQrToken(token: String): Booking?

    @Query("SELECT seatNumber FROM bookings WHERE origin = :origin AND destination = :destination AND busName = :busName AND travelDate = :travelDate AND status != 'Cancelled'")
    fun getBookedSeats(origin: String, destination: String, busName: String, travelDate: String): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Update
    suspend fun updateBooking(booking: Booking)

    @Query("UPDATE bookings SET status = 'Boarded', boardedTimestampPkt = :boardedTime WHERE bookingReference = :reference")
    suspend fun markAsBoarded(reference: String, boardedTime: String)

    @Query("UPDATE bookings SET status = 'Cancelled' WHERE bookingReference = :reference")
    suspend fun cancelBooking(reference: String)

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun getTotalBookingsCount(): Int

    @Query("SELECT COALESCE(SUM(fare), 0) FROM bookings WHERE status != 'Cancelled'")
    suspend fun getTotalRevenue(): Long

    @Query("SELECT COUNT(*) FROM bookings WHERE status = 'Boarded'")
    suspend fun getBoardedCount(): Int
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem): Long

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface SecurityAlertDao {
    @Query("SELECT * FROM security_alerts ORDER BY sentTimestamp DESC")
    fun getAllAlerts(): Flow<List<SecurityAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SecurityAlert): Long
}

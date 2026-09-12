package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        BusRoute::class,
        Booking::class,
        NotificationItem::class,
        SecurityAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZaMoversDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun routeDao(): RouteDao
    abstract fun bookingDao(): BookingDao
    abstract fun notificationDao(): NotificationDao
    abstract fun securityAlertDao(): SecurityAlertDao

    companion object {
        @Volatile
        private var INSTANCE: ZaMoversDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ZaMoversDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZaMoversDatabase::class.java,
                    "za_movers_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: ZaMoversDatabase) {
            val routeDao = database.routeDao()
            val bookingDao = database.bookingDao()
            val notifDao = database.notificationDao()
            val userDao = database.userDao()

            // Preload default authenticated user
            userDao.insertOrUpdateUser(
                User(
                    email = "balochistanalert331@gmail.com",
                    displayName = "Haq Baloch",
                    role = "OWNER_ADMIN",
                    avatarInitials = "HB",
                    phone = "+92 333 7894521",
                    totalTrips = 12,
                    upcomingTrips = 1,
                    rating = 4.9
                )
            )

            // Preload routes matching reference screenshot
            val routes = listOf(
                BusRoute(
                    origin = "Kharan",
                    destination = "Quetta",
                    busName = "ZA Express",
                    busCode = "ZE",
                    busModel = "Yutong Master",
                    departureTime = "08:00 AM",
                    duration = "6h 30m",
                    fare = 2500,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Kharan",
                    destination = "Quetta",
                    busName = "Baloch Coach",
                    busCode = "BC",
                    busModel = "Daewoo",
                    departureTime = "10:00 AM",
                    duration = "6h 45m",
                    fare = 2400,
                    totalSeats = 44,
                    isAc = false
                ),
                BusRoute(
                    origin = "Kharan",
                    destination = "Quetta",
                    busName = "Quetta Lines",
                    busCode = "QL",
                    busModel = "Yutong Nova",
                    departureTime = "02:00 PM",
                    duration = "6h 30m",
                    fare = 2400,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Kharan",
                    destination = "Quetta",
                    busName = "Kharan Super",
                    busCode = "KS",
                    busModel = "Higer Luxury",
                    departureTime = "09:00 PM",
                    duration = "6h 15m",
                    fare = 2600,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Quetta",
                    destination = "Kharan",
                    busName = "ZA Express",
                    busCode = "ZE",
                    busModel = "Yutong Master",
                    departureTime = "07:30 AM",
                    duration = "6h 30m",
                    fare = 2500,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Quetta",
                    destination = "Karachi",
                    busName = "ZA Royal Class",
                    busCode = "ZR",
                    busModel = "Yutong Master Sleeper",
                    departureTime = "06:00 PM",
                    duration = "10h 30m",
                    fare = 3500,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Karachi",
                    destination = "Quetta",
                    busName = "ZA Royal Class",
                    busCode = "ZR",
                    busModel = "Yutong Master Sleeper",
                    departureTime = "07:00 PM",
                    duration = "10h 30m",
                    fare = 3500,
                    totalSeats = 44,
                    isAc = true
                ),
                BusRoute(
                    origin = "Quetta",
                    destination = "Dalbandin",
                    busName = "Chagai Express",
                    busCode = "CE",
                    busModel = "Daewoo Express",
                    departureTime = "09:00 AM",
                    duration = "7h 00m",
                    fare = 2800,
                    totalSeats = 44,
                    isAc = true
                )
            )
            routes.forEach { routeDao.insertRoute(it) }

            // Preload sample booking matching reference screenshot (ZA7845293)
            bookingDao.insertBooking(
                Booking(
                    bookingReference = "ZA7845293",
                    userEmail = "balochistanalert331@gmail.com",
                    passengerName = "Haq Baloch",
                    passengerPhone = "+92 333 7894521",
                    origin = "Kharan",
                    destination = "Quetta",
                    busName = "ZA Express",
                    busCode = "ZE",
                    seatNumber = 10,
                    fare = 2500,
                    travelDate = "Wed, 10 Sep 2026",
                    departureTimePkt = "08:00 AM PKT",
                    bookingTimestampPkt = "10 Sep 2026, 01:21 AM PKT",
                    status = "Confirmed",
                    qrSecurityToken = "ZAM-ZA7845293-SEC-99824"
                )
            )

            // Also add some booked seats for seat selection preview matching reference image 5
            val sampleBookedSeats = listOf(6, 10, 15, 16, 28, 33, 34, 40)
            sampleBookedSeats.forEach { seatNo ->
                if (seatNo != 10) {
                    bookingDao.insertBooking(
                        Booking(
                            bookingReference = "ZA" + (7000000 + seatNo * 1337),
                            userEmail = "passenger$seatNo@example.com",
                            passengerName = "Passenger $seatNo",
                            passengerPhone = "+92 300 00000$seatNo",
                            origin = "Kharan",
                            destination = "Quetta",
                            busName = "ZA Express",
                            busCode = "ZE",
                            seatNumber = seatNo,
                            fare = 2500,
                            travelDate = "Wed, 10 Sep 2026",
                            departureTimePkt = "08:00 AM PKT",
                            bookingTimestampPkt = "10 Sep 2026, 01:00 AM PKT",
                            status = "Confirmed",
                            qrSecurityToken = "ZAM-SEAT$seatNo-SEC"
                        )
                    )
                }
            }

            // Preload notifications matching reference screenshot 1
            val notifications = listOf(
                NotificationItem(
                    title = "Booking Confirmed",
                    message = "Your ticket ZA7845293 has been confirmed.",
                    timeAgo = "2m",
                    category = "booking",
                    isRead = false
                ),
                NotificationItem(
                    title = "Ticket Generated",
                    message = "Your e-ticket is ready. Tap to view or download.",
                    timeAgo = "5m",
                    category = "ticket",
                    isRead = false
                ),
                NotificationItem(
                    title = "Bus Schedule Update",
                    message = "Departure time changed from 08:00 AM to 08:30 AM.",
                    timeAgo = "1h",
                    category = "schedule",
                    isRead = true
                ),
                NotificationItem(
                    title = "Important Notice",
                    message = "Please arrive at least 30 minutes early at the bus terminal.",
                    timeAgo = "3h",
                    category = "notice",
                    isRead = true
                ),
                NotificationItem(
                    title = "Special Offer \uD83C\uDF89",
                    message = "New routes coming soon! Stay tuned with ZA Movers.",
                    timeAgo = "1d",
                    category = "offer",
                    isRead = true
                )
            )
            notifications.forEach { notifDao.insertNotification(it) }
        }
    }
}

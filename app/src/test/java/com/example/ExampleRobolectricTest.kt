package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ZaMoversDatabase
import com.example.data.repository.ZaMoversRepository
import com.example.data.repository.QrVerificationResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ZA MOVERS", appName)
    }

    @Test
    fun `verify admin authorization and security email generation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = ZaMoversDatabase.getDatabase(context, this)
        val repository = ZaMoversRepository(database)

        // Seed initial data
        ZaMoversDatabase.populateInitialData(database)

        // Authenticate as owner
        val (user, alert) = repository.authenticateWithGoogle(
            ZaMoversRepository.OWNER_EMAIL,
            "Haq Baloch"
        )
        assertEquals("OWNER_ADMIN", user.role)
        assertTrue(repository.verifyAdminAccess(user.email))
        assertNotNull(alert)
        assertEquals(ZaMoversRepository.OWNER_EMAIL, alert.userEmail)

        // Create booking and verify QR token boarding
        val booking = repository.createBooking(
            userEmail = user.email,
            passengerName = "Test Passenger",
            passengerPhone = "+92 333 1234567",
            origin = "Kharan",
            destination = "Quetta",
            busName = "ZA Express",
            busCode = "ZE",
            seatNumber = 12,
            fare = 2500,
            travelDate = "Sun, 13 Sep 2026",
            departureTime = "08:00 AM"
        )
        assertEquals("Confirmed", booking.status)

        // Admin scans QR code to board passenger
        val scanResult = repository.verifyAndBoardTicket(user.email, booking.qrSecurityToken)
        assertTrue(scanResult is QrVerificationResult.Success)

        // Scanning second time should trigger duplicate boarding warning
        val duplicateScan = repository.verifyAndBoardTicket(user.email, booking.qrSecurityToken)
        assertTrue(duplicateScan is QrVerificationResult.AlreadyBoarded)
    }
}

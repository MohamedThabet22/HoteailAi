package com.example.hoteailai.domain.repository

import com.example.hoteailai.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Result<String>
    fun getBookingHistory(userId: String): Flow<List<Booking>>
    fun getBookingById(id: String): Flow<Booking?>
    suspend fun cancelBooking(bookingId: String): Result<Unit>
}

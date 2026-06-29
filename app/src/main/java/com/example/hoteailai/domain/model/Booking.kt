package com.example.hoteailai.domain.model

data class Booking(
    val id: String = "",
    val hotelId: String = "",
    val hotelName: String = "",
    val hotelImageUrl: String = "",
    val userId: String = "",
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val guestCount: Int = 1,
    val roomType: String = "",
    val totalPrice: Double = 0.0,
    val status: String = "Pending", // Pending, Confirmed, Cancelled
    val bookingDate: String = "",
    val location: String = ""
)

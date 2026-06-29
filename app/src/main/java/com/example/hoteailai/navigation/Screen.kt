package com.example.hoteailai.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object HotelDetails : Screen("hotel_details/{hotelId}") {
        fun createRoute(hotelId: String) = "hotel_details/$hotelId"
    }
    object Booking : Screen("booking/{hotelId}") {
        fun createRoute(hotelId: String) = "booking/$hotelId"
    }
    object Profile : Screen("profile")
    object Wishlist : Screen("wishlist")
    object Trips : Screen("trips")
    object Search : Screen("search")
    object Checkout : Screen("checkout/{hotelId}?checkIn={checkIn}&duration={duration}&guests={guests}") {
        fun createRoute(hotelId: String, checkIn: Long, duration: Int, guests: Int) = 
            "checkout/$hotelId?checkIn=$checkIn&duration=$duration&guests=$guests"
    }
    object Confirmation : Screen("confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "confirmation/$bookingId"
    }
}

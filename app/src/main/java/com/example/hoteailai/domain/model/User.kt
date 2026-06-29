package com.example.hoteailai.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String = "",
    val membershipTier: String = "Regular", // Regular, Elite, Premium
    val bookingsCount: Int = 0,
    val wishlistCount: Int = 0,
    val points: Int = 0
)

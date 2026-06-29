package com.example.hoteailai.domain.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Hotel(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("location") @set:PropertyName("location") var location: String = "",
    @get:PropertyName("city") @set:PropertyName("city") var city: String = "",
    @get:PropertyName("country") @set:PropertyName("country") var country: String = "",
    @get:PropertyName("rating") @set:PropertyName("rating") var rating: Double = 0.0,
    @get:PropertyName("reviewCount") @set:PropertyName("reviewCount") var reviewCount: Int = 0,
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("pricePerNight") @set:PropertyName("pricePerNight") var pricePerNight: Double = 0.0,
    @get:PropertyName("imageUrls") @set:PropertyName("imageUrls") var imageUrls: List<String> = emptyList(),
    @get:PropertyName("amenities") @set:PropertyName("amenities") var amenities: List<String> = emptyList(),
    @get:PropertyName("categoryId") @set:PropertyName("categoryId") var categoryId: String = "",
    
    @get:PropertyName("isFeatured")
    @set:PropertyName("isFeatured")
    var isFeatured: Boolean = false,
    
    @get:PropertyName("isPopular")
    @set:PropertyName("isPopular")
    var isPopular: Boolean = false,

    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var isFavorite: Boolean = false,

    @get:PropertyName("latitude") @set:PropertyName("latitude") var latitude: Double = 0.0,
    @get:PropertyName("longitude") @set:PropertyName("longitude") var longitude: Double = 0.0,
    @get:PropertyName("reviews") @set:PropertyName("reviews") var reviews: List<Review> = emptyList()
)

@IgnoreExtraProperties
data class Review(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = "",
    @get:PropertyName("userName") @set:PropertyName("userName") var userName: String = "",
    @get:PropertyName("userImageUrl") @set:PropertyName("userImageUrl") var userImageUrl: String = "",
    @get:PropertyName("rating") @set:PropertyName("rating") var rating: Double = 0.0,
    @get:PropertyName("comment") @set:PropertyName("comment") var comment: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = ""
)

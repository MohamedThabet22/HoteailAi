package com.example.hoteailai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.model.Review

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val city: String,
    val country: String,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val pricePerNight: Double,
    val imageUrls: List<String>,
    val amenities: List<String>,
    val categoryId: String,
    val isFeatured: Boolean,
    val isPopular: Boolean,
    val isFavorite: Boolean = false,
    val latitude: Double,
    val longitude: Double,
    val reviews: List<Review>,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun HotelEntity.toDomain() = Hotel(
    id = id,
    name = name,
    location = location,
    city = city,
    country = country,
    rating = rating,
    reviewCount = reviewCount,
    description = description,
    pricePerNight = pricePerNight,
    imageUrls = imageUrls,
    amenities = amenities,
    categoryId = categoryId,
    isFeatured = isFeatured,
    isPopular = isPopular,
    isFavorite = isFavorite,
    latitude = latitude,
    longitude = longitude,
    reviews = reviews
)

fun Hotel.toEntity() = HotelEntity(
    id = id,
    name = name,
    location = location,
    city = city,
    country = country,
    rating = rating,
    reviewCount = reviewCount,
    description = description,
    pricePerNight = pricePerNight,
    imageUrls = imageUrls,
    amenities = amenities,
    categoryId = categoryId,
    isFeatured = isFeatured,
    isPopular = isPopular,
    isFavorite = isFavorite,
    latitude = latitude,
    longitude = longitude,
    reviews = reviews
)

package com.example.hoteailai.domain.repository

import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.model.Offer
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getFeaturedHotels(): Flow<List<Hotel>>
    fun getPopularHotels(): Flow<List<Hotel>>
    fun getRecommendedHotels(): Flow<List<Hotel>>
    fun getCategories(): Flow<List<Category>>
    fun getOffers(): Flow<List<Offer>>
    fun searchHotels(query: String): Flow<List<Hotel>>
    fun getHotelById(id: String): Flow<Hotel?>
    suspend fun toggleFavorite(hotelId: String)
    fun getFavoriteHotels(): Flow<List<Hotel>>
}

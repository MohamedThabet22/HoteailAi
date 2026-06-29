package com.example.hoteailai.data.local.dao

import androidx.room.*
import com.example.hoteailai.data.local.entity.CategoryEntity
import com.example.hoteailai.data.local.entity.HotelEntity
import com.example.hoteailai.data.local.entity.OfferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {
    @Query("SELECT * FROM hotels")
    fun getAllHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE isFeatured = 1")
    fun getFeaturedHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE isPopular = 1")
    fun getPopularHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE isFavorite = 1")
    fun getFavoriteHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE categoryId = :categoryId")
    fun getHotelsByCategory(categoryId: String): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE id = :id")
    fun getHotelByIdFlow(id: String): Flow<HotelEntity?>

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getHotelById(id: String): HotelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<HotelEntity>)

    @Query("UPDATE hotels SET isFavorite = :isFavorite WHERE id = :hotelId")
    suspend fun updateFavoriteStatus(hotelId: String, isFavorite: Boolean)

    @Query("DELETE FROM hotels")
    suspend fun deleteAllHotels()

    // Categories
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    // Offers
    @Query("SELECT * FROM offers")
    fun getAllOffers(): Flow<List<OfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferEntity>)
}

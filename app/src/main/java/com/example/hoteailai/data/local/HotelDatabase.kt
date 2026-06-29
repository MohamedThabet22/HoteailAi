package com.example.hoteailai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hoteailai.data.local.converter.Converters
import com.example.hoteailai.data.local.dao.HotelDao
import com.example.hoteailai.data.local.entity.CategoryEntity
import com.example.hoteailai.data.local.entity.HotelEntity
import com.example.hoteailai.data.local.entity.OfferEntity

@Database(
    entities = [HotelEntity::class, CategoryEntity::class, OfferEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HotelDatabase : RoomDatabase() {
    abstract val hotelDao: HotelDao

    companion object {
        const val DATABASE_NAME = "hotel_db"
    }
}

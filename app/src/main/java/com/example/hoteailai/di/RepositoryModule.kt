package com.example.hoteailai.di

import com.example.hoteailai.data.repository.AuthRepositoryImpl
import com.example.hoteailai.data.repository.BookingRepositoryImpl
import com.example.hoteailai.data.repository.HotelRepositoryImpl
import com.example.hoteailai.domain.repository.AuthRepository
import com.example.hoteailai.domain.repository.BookingRepository
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        hotelRepositoryImpl: HotelRepositoryImpl
    ): HotelRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository
}

package com.example.hoteailai.data.repository

import com.example.hoteailai.domain.model.Booking
import com.example.hoteailai.domain.repository.BookingRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    override suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val docRef = firestore.collection("bookings").add(booking).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBookingHistory(userId: String): Flow<List<Booking>> = flow {
        val snapshot = firestore.collection("bookings")
            .whereEqualTo("userId", userId)
            .get().await()
        emit(snapshot.toObjects(Booking::class.java))
    }

    override fun getBookingById(id: String): Flow<Booking?> = flow {
        val snapshot = firestore.collection("bookings").document(id).get().await()
        emit(snapshot.toObject(Booking::class.java))
    }

    override suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return try {
            firestore.collection("bookings").document(bookingId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

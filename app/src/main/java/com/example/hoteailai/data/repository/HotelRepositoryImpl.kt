package com.example.hoteailai.data.repository

import android.util.Log
import com.example.hoteailai.data.local.dao.HotelDao
import com.example.hoteailai.data.local.entity.toDomain
import com.example.hoteailai.data.local.entity.toEntity
import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.model.Offer
import com.example.hoteailai.domain.repository.HotelRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val hotelDao: HotelDao
) : HotelRepository {

    private val TAG = "HotelRepository"
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Start continuous background sync
        syncHotelsFromFirebase()
        syncUserFavorites()
    }

    override fun getFeaturedHotels(): Flow<List<Hotel>> = 
        hotelDao.getFeaturedHotels().map { entities -> entities.map { it.toDomain() } }

    override fun getPopularHotels(): Flow<List<Hotel>> = 
        hotelDao.getPopularHotels().map { entities -> entities.map { it.toDomain() } }

    override fun getRecommendedHotels(): Flow<List<Hotel>> = 
        hotelDao.getAllHotels().map { entities -> entities.map { it.toDomain() } }

    override fun getCategories(): Flow<List<Category>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(Category::class.java) }
                repositoryScope.launch {
                    hotelDao.insertCategories(categories.map { it.toEntity() })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Categories sync error: ${error.message}")
            }
        }
        firebaseDatabase.getReference("categories").addValueEventListener(listener)
        
        val dbJob = repositoryScope.launch {
            hotelDao.getAllCategories().collect { entities ->
                trySend(entities.map { it.toDomain() })
            }
        }
        
        awaitClose { 
            firebaseDatabase.getReference("categories").removeEventListener(listener)
            dbJob.cancel()
        }
    }

    override fun getOffers(): Flow<List<Offer>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offers = snapshot.children.mapNotNull { it.getValue(Offer::class.java) }
                repositoryScope.launch {
                    hotelDao.insertOffers(offers.map { it.toEntity() })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Offers sync error: ${error.message}")
            }
        }
        firebaseDatabase.getReference("offers").addValueEventListener(listener)
        
        val dbJob = repositoryScope.launch {
            hotelDao.getAllOffers().collect { entities ->
                trySend(entities.map { it.toDomain() })
            }
        }
        
        awaitClose { 
            firebaseDatabase.getReference("offers").removeEventListener(listener)
            dbJob.cancel()
        }
    }

    override fun searchHotels(query: String): Flow<List<Hotel>> = hotelDao.getAllHotels().map { hotels ->
        hotels.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.location.contains(query, ignoreCase = true) ||
            it.city.contains(query, ignoreCase = true)
        }.map { it.toDomain() }
    }

    override fun getHotelById(id: String): Flow<Hotel?> = channelFlow {
        hotelDao.getHotelByIdFlow(id).collect { entity ->
            send(entity?.toDomain())
        }
    }

    override suspend fun toggleFavorite(hotelId: String) {
        val userId = auth.currentUser?.uid ?: return
        val hotel = hotelDao.getHotelById(hotelId) ?: return
        val newFavoriteStatus = !hotel.isFavorite
        
        // Update Local
        hotelDao.updateFavoriteStatus(hotelId, newFavoriteStatus)
        
        // Update Remote
        try {
            val favoriteRef = firebaseDatabase.getReference("users").child(userId).child("favorites").child(hotelId)
            if (newFavoriteStatus) {
                favoriteRef.setValue(true).await()
            } else {
                favoriteRef.removeValue().await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favorite in Firebase: ${e.message}")
        }
    }

    override fun getFavoriteHotels(): Flow<List<Hotel>> = 
        hotelDao.getFavoriteHotels().map { entities -> entities.map { it.toDomain() } }

    private fun syncHotelsFromFirebase() {
        firebaseDatabase.getReference("hotels").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repositoryScope.launch {
                    val hotels = snapshot.children.mapNotNull { 
                        try {
                            it.getValue(Hotel::class.java)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing hotel ${it.key}: ${e.message}")
                            null
                        }
                    }
                    if (hotels.isNotEmpty()) {
                        // Get current favorite status to preserve it
                        val currentHotels = hotelDao.getAllHotels().first()
                        val favoriteStatusMap = currentHotels.associate { it.id to it.isFavorite }
                        
                        val entities = hotels.map { hotel ->
                            hotel.toEntity().copy(isFavorite = favoriteStatusMap[hotel.id] ?: false)
                        }
                        hotelDao.insertHotels(entities)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Hotels sync cancelled: ${error.message}")
            }
        })
    }

    private fun syncUserFavorites() {
        // Listen to auth changes to restart sync
        auth.addAuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                firebaseDatabase.getReference("users").child(userId).child("favorites")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            repositoryScope.launch {
                                val favoriteIds = snapshot.children.mapNotNull { it.key }.toSet()
                                val allHotels = hotelDao.getAllHotels().first()
                                allHotels.forEach { hotel ->
                                    val isFav = favoriteIds.contains(hotel.id)
                                    if (hotel.isFavorite != isFav) {
                                        hotelDao.updateFavoriteStatus(hotel.id, isFav)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
    }
}

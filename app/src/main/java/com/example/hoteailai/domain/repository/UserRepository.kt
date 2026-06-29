package com.example.hoteailai.domain.repository

import com.example.hoteailai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(userId: String): Flow<User?>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String>
}

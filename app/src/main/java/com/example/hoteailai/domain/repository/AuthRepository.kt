package com.example.hoteailai.domain.repository

import com.example.hoteailai.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<User>
}

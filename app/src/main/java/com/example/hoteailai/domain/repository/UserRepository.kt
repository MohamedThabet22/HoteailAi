package com.example.hoteailai.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isFirstTime(): Flow<Boolean>
    suspend fun setFirstTimeCompleted()
}

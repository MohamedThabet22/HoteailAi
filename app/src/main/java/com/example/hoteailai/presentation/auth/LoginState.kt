package com.example.hoteailai.presentation.auth

import com.example.hoteailai.domain.model.User

data class AuthState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

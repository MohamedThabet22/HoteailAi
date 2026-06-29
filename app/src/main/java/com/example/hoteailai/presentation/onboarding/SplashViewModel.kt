package com.example.hoteailai.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.repository.AuthRepository
import com.example.hoteailai.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isLoggedIn: Boolean = false,
    val isFirstTime: Boolean = true,
    val isReady: Boolean = false
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state = _state.asStateFlow()

    init {
        checkStatus()
    }

    private fun checkStatus() {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                userRepository.isFirstTime()
            ) { user, firstTime ->
                SplashState(
                    isLoggedIn = user != null,
                    isFirstTime = firstTime,
                    isReady = true
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun setFirstTimeCompleted() {
        viewModelScope.launch {
            userRepository.setFirstTimeCompleted()
        }
    }
}

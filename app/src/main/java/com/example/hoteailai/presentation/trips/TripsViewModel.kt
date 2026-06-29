package com.example.hoteailai.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Booking
import com.example.hoteailai.domain.repository.AuthRepository
import com.example.hoteailai.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripsState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripsState())
    val state = _state.asStateFlow()

    init {
        loadTrips()
    }

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadTrips() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.getCurrentUser().flatMapLatest { user ->
                if (user != null) {
                    bookingRepository.getBookingHistory(user.id)
                } else {
                    flowOf(emptyList())
                }
            }.catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }.collect { bookings ->
                _state.update { it.copy(bookings = bookings, isLoading = false) }
            }
        }
    }
}

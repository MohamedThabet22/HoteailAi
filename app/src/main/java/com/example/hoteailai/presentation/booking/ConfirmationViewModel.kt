package com.example.hoteailai.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Booking
import com.example.hoteailai.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfirmationState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmationState())
    val state = _state.asStateFlow()

    private val bookingId: String? = savedStateHandle["bookingId"]

    init {
        bookingId?.let { loadBooking(it) }
    }

    private fun loadBooking(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            bookingRepository.getBookingById(id).collect { booking ->
                _state.update { it.copy(booking = booking, isLoading = false) }
            }
        }
    }
}

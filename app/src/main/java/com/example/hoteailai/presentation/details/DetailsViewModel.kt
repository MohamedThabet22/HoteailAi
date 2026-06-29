package com.example.hoteailai.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DetailsState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val checkInDate: Long = System.currentTimeMillis(),
    val durationDays: Int = 1,
    val guestCount: Int = 1
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    private val hotelId: String? = savedStateHandle["hotelId"]

    init {
        hotelId?.let { loadHotelDetails(it) }
    }

    private fun loadHotelDetails(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            hotelRepository.getHotelById(id)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { hotel ->
                    _state.update { it.copy(hotel = hotel, isLoading = false) }
                }
        }
    }

    fun toggleFavorite() {
        hotelId?.let { id ->
            viewModelScope.launch {
                hotelRepository.toggleFavorite(id)
            }
        }
    }

    fun onDateChange(date: Long) {
        _state.update { it.copy(checkInDate = date) }
    }

    fun onDurationChange(days: Int) {
        if (days >= 1) {
            _state.update { it.copy(durationDays = days) }
        }
    }

    fun onGuestCountChange(count: Int) {
        if (count >= 1) {
            _state.update { it.copy(guestCount = count) }
        }
    }
}

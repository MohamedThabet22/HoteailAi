package com.example.hoteailai.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Booking
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.repository.AuthRepository
import com.example.hoteailai.domain.repository.BookingRepository
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val isBookingInProgress: Boolean = false,
    val bookingId: String? = null,
    val error: String? = null,
    val paymentMethod: String = "Credit Card",
    val cardholderName: String = "",
    val cardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val checkInDate: Long = 0,
    val durationDays: Int = 1,
    val guestCount: Int = 1
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    private val hotelId: String? = savedStateHandle["hotelId"]
    private val checkIn: Long = savedStateHandle["checkIn"] ?: 0L
    private val duration: Int = savedStateHandle["duration"] ?: 1
    private val guests: Int = savedStateHandle["guests"] ?: 1
    
    private var userId: String = ""

    init {
        hotelId?.let { loadHotel(it) }
        fetchUser()
        _state.update { it.copy(
            checkInDate = checkIn,
            durationDays = duration,
            guestCount = guests
        ) }
    }

    private fun fetchUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                userId = user?.id ?: ""
            }
        }
    }

    private fun loadHotel(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            hotelRepository.getHotelById(id).collect { hotel ->
                _state.update { it.copy(hotel = hotel, isLoading = false) }
            }
        }
    }

    fun onPaymentMethodChange(method: String) {
        _state.update { it.copy(paymentMethod = method) }
    }

    fun onCardDetailsChange(name: String, number: String, expiry: String, cvv: String) {
        _state.update { it.copy(cardholderName = name, cardNumber = number, expiryDate = expiry, cvv = cvv) }
    }

    fun confirmBooking() {
        viewModelScope.launch {
            _state.update { it.copy(isBookingInProgress = true) }
            val hotel = _state.value.hotel ?: return@launch
            val sdf = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            val checkInStr = sdf.format(java.util.Date(_state.value.checkInDate))
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = _state.value.checkInDate
            cal.add(java.util.Calendar.DAY_OF_YEAR, _state.value.durationDays)
            val checkOutStr = sdf.format(cal.time)

            val booking = Booking(
                userId = userId,
                hotelId = hotel.id,
                hotelName = hotel.name,
                hotelImageUrl = hotel.imageUrls.firstOrNull() ?: "",
                checkInDate = checkInStr,
                checkOutDate = checkOutStr,
                guestCount = _state.value.guestCount,
                totalPrice = hotel.pricePerNight * _state.value.durationDays + 45.0 + 124.50,
                location = hotel.location,
                status = "Confirmed"
            )
            val result = bookingRepository.createBooking(booking)
            result.onSuccess { id ->
                _state.update { it.copy(isBookingInProgress = false, bookingId = id) }
            }.onFailure { e ->
                _state.update { it.copy(isBookingInProgress = false, error = e.message) }
            }
        }
    }
}

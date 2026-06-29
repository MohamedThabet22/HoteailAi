package com.example.hoteailai.presentation.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistState(
    val favoriteHotels: List<Hotel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WishlistState())
    val state = _state.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            hotelRepository.getFavoriteHotels()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { favorites ->
                    _state.update { it.copy(favoriteHotels = favorites, isLoading = false) }
                }
        }
    }

    fun toggleFavorite(hotelId: String) {
        viewModelScope.launch {
            hotelRepository.toggleFavorite(hotelId)
        }
    }
}

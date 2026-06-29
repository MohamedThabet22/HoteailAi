package com.example.hoteailai.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.model.Offer
import com.example.hoteailai.domain.model.User
import com.example.hoteailai.domain.repository.AuthRepository
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val currentUser: User? = null,
    val featuredHotels: List<Hotel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val offers: List<Offer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow().map { state ->
        if (state.selectedCategoryId == null) state
        else state.copy(
            featuredHotels = state.featuredHotels.filter { it.categoryId == state.selectedCategoryId }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            combine(
                authRepository.getCurrentUser(),
                hotelRepository.getFeaturedHotels().conflate(),
                hotelRepository.getCategories().conflate(),
                hotelRepository.getOffers().conflate()
            ) { user, featured, categories, offers ->
                _state.update { it.copy(
                    currentUser = user,
                    featuredHotels = featured,
                    categories = categories.filter { it.name != "Luxury" },
                    offers = offers,
                    isLoading = false
                ) }
            }.catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }.collect()
        }
    }

    fun toggleFavorite(hotelId: String) {
        viewModelScope.launch {
            hotelRepository.toggleFavorite(hotelId)
        }
    }

    fun onCategorySelect(categoryId: String) {
        _state.update { 
            it.copy(selectedCategoryId = if (it.selectedCategoryId == categoryId) null else categoryId)
        }
    }
}

package com.example.hoteailai.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val hotels: List<Hotel> = emptyList(),
    val categories: List<Category> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val recentSearches: List<String> = listOf("Paris", "London", "Tokyo", "New York"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val selectedCategoryId: String? = null,
    val minPrice: Double = 500.0,
    val maxPrice: Double = 10000.0,
    val selectedExperience: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
        search()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            hotelRepository.getCategories().collect { categories ->
                _state.update { it.copy(categories = categories.filter { it.name != "Luxury" }) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        updateSuggestions(query)
        if (query.length > 2) {
            search()
        }
    }

    private fun updateSuggestions(query: String) {
        if (query.isEmpty()) {
            _state.update { it.copy(suggestions = emptyList()) }
            return
        }
        val allSuggestions = listOf("Paris, France", "London, UK", "New York, USA", "Tokyo, Japan", "Rome, Italy")
        val filtered = allSuggestions.filter { it.contains(query, ignoreCase = true) }
        _state.update { it.copy(suggestions = filtered) }
    }

    fun onCategorySelect(categoryId: String) {
        _state.update { 
            val newSelection = if (it.selectedCategoryId == categoryId) null else categoryId
            it.copy(selectedCategoryId = newSelection) 
        }
        search()
    }

    fun onPriceRangeChange(min: Double, max: Double) {
        _state.update { it.copy(minPrice = min, maxPrice = max) }
    }

    fun onExperienceSelect(experience: String) {
        _state.update { it.copy(selectedExperience = experience) }
    }

    fun search() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            hotelRepository.searchHotels(_state.value.query)
                .map { hotels ->
                    // Apply category filter locally
                    val categoryId = _state.value.selectedCategoryId
                    if (categoryId != null) {
                        hotels.filter { it.categoryId == categoryId }
                    } else {
                        hotels
                    }
                }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { hotels ->
                    _state.update { it.copy(hotels = hotels, isLoading = false) }
                }
        }
    }

    fun toggleFavorite(hotelId: String) {
        viewModelScope.launch {
            hotelRepository.toggleFavorite(hotelId)
        }
    }
}

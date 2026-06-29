package com.example.hoteailai.presentation.home

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.domain.model.Offer
import com.example.hoteailai.presentation.components.BeautifulProgressIndicator
import com.example.hoteailai.presentation.components.CategoryList
import com.example.hoteailai.presentation.components.HotelCard
import com.example.hoteailai.presentation.components.HotelCardShimmer
import com.example.hoteailai.presentation.components.LoadingScreen
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold
import com.example.hoteailai.R

@Composable
fun HomeScreen(
    onHotelClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                userName = state.currentUser?.name ?: "Guest",
                userImageUrl = state.currentUser?.profileImageUrl,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
            ) {
                SearchBar(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .clickable { onSearchClick() }
                )
                
                AnimatedVisibility(
                    visible = state.categories.isNotEmpty(),
                    enter = fadeIn() + expandVertically()
                ) {
                    Column {
                        SectionHeader(title = "Collections", onSeeAllClick = {})
                        CategoryList(
                            categories = state.categories,
                            selectedCategoryId = state.selectedCategoryId,
                            onCategoryClick = { viewModel.onCategorySelect(it) }
                        )
                    }
                }
                
                SectionHeader(title = "Featured Stays", onSeeAllClick = {})
                AnimatedVisibility(
                    visible = !state.isLoading || state.featuredHotels.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it / 2 } + expandVertically()
                ) {
                    if (state.isLoading && state.featuredHotels.isEmpty()) {
                        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            items(3) { HotelCardShimmer() }
                        }
                    } else {
                        FeaturedHotelsList(
                            hotels = state.featuredHotels, 
                            onHotelClick = onHotelClick,
                            onFavoriteClick = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
                
                SectionHeader(title = "Exclusive Privileges", onSeeAllClick = {})
                AnimatedVisibility(
                    visible = !state.isLoading || state.offers.isNotEmpty(),
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    if (state.isLoading && state.offers.isEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            repeat(2) { HotelCardShimmer() }
                        }
                    } else {
                        OffersList(offers = state.offers)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            AnimatedVisibility(
                visible = state.isLoading && state.categories.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    userImageUrl: String?,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.avator),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { onProfileClick() },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "YOUR EXPERIENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Hotelio",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        IconButton(onClick = { /* Notification */ }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PrimaryBlue)
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Where would you like to escape?", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Surface(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp), color = PrimaryBlue) {
                Icon(Icons.Default.FilterList, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "View All",
            color = SecondaryGold,
            modifier = Modifier.clickable { onSeeAllClick() },
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Composable
fun FeaturedHotelsList(
    hotels: List<Hotel>, 
    onHotelClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(hotels.size) { index ->
            val hotel = hotels[index]
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(index * 100L)
                visible = true
            }
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInHorizontally { it / 2 }
            ) {
                HotelCard(
                    hotel = hotel, 
                    onClick = { onHotelClick(hotel.id) },
                    onFavoriteClick = { onFavoriteClick(hotel.id) },
                    modifier = Modifier.width(280.dp)
                )
            }
        }
    }
}

@Composable
fun OffersList(offers: List<Offer>) {
    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        offers.forEach { offer ->
            OfferCard(offer = offer)
        }
    }
}

@Composable
fun OfferCard(offer: Offer) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(16.dp), color = PrimaryBlue) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = SecondaryGold, modifier = Modifier.padding(16.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(text = offer.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text(text = offer.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "PROMO CODE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = SecondaryGold.copy(alpha = 0.1f)) {
                        Text(text = offer.promoCode, color = SecondaryGold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

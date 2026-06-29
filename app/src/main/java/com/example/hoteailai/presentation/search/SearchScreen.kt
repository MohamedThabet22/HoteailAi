package com.example.hoteailai.presentation.search

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.presentation.components.*
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onHotelClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Map */ },
                icon = { Icon(Icons.Default.Map, contentDescription = null, tint = SecondaryGold) },
                text = { Text("EXPLORE MAP", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFF4C6793),
                contentColor = Color.White,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            SearchHeader(
                query = state.query,
                onQueryChange = viewModel::onQueryChange
            )

            CategoryList(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategoryClick = viewModel::onCategorySelect
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.hotels.size} CURATED STAYS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sort by: ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = "Relevance", style = MaterialTheme.typography.labelSmall, color = SecondaryGold, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BeautifulProgressIndicator()
                }
            } else if (state.hotels.isEmpty()) {
                EmptyState(
                    message = "No Hotels Found",
                    description = "Try adjusting your filters or search query to find what you're looking for.",
                    onRetry = { viewModel.search() }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(state.hotels) { hotel ->
                        if (hotel.isFeatured) {
                            FeaturedHotelCard(
                                hotel = hotel, 
                                onClick = { onHotelClick(hotel.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(hotel.id) }
                            )
                        } else {
                            RegularHotelCard(
                                hotel = hotel, 
                                onClick = { onHotelClick(hotel.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(hotel.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            FilterBottomSheetContent(
                onApply = { scope.launch { sheetState.hide() }.invokeOnCompletion { showFilters = false } },
                onReset = { /* Reset */ }
            )
        }
    }
}

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        color = PrimaryBlue,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "YOUR DESTINATION",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (query.isEmpty()) "Paris, France" else query,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Text(text = "Oct 12 - 15", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                Text(text = "2 Guests", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun FeaturedHotelCard(hotel: Hotel, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(280.dp).clickable { onClick() }) {
                AsyncImage(
                    model = hotel.imageUrls.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Badge
                Surface(
                    modifier = Modifier.padding(20.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "FEATURED ESTATE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Favorite Button
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .clickable { onFavoriteClick() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Icon(
                        imageVector = if (hotel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (hotel.isFavorite) Color.Red else PrimaryBlue,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Domain, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PALACE DISTINCTION",
                        color = SecondaryGold,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(text = hotel.location, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = BackgroundLight)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "RATING", value = "${hotel.rating} \u2B50")
                    InfoItem(label = "REVIEWS", value = "${hotel.reviewCount / 1000.0}k")
                    InfoItem(label = "TOTAL STAY", value = "$${String.format("%,.0f", hotel.pricePerNight * 3)}")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AmenityIcon(Icons.Default.Wifi, "FREE WIFI")
                    AmenityIcon(Icons.Default.Pool, "PRIVATE POOL")
                    AmenityIcon(Icons.Default.Restaurant, "KITCHEN")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                PrimaryButton(
                    text = "Secure Your Experience \u2192",
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun RegularHotelCard(hotel: Hotel, onClick: () -> Unit, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(220.dp).clickable { onClick() }) {
                AsyncImage(
                    model = hotel.imageUrls.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "BOUTIQUE LUXE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                // Favorite Button
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clickable { onFavoriteClick() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Icon(
                        imageVector = if (hotel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (hotel.isFavorite) Color.Red else PrimaryBlue,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = hotel.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text(
                            text = hotel.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${hotel.rating} \u2B50", fontWeight = FontWeight.Bold)
                        Text(text = "892 reviews", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hotel.amenities.take(3).forEach { amenity ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BackgroundLight
                        ) {
                            Text(
                                text = amenity.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(text = "FROM", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            text = "$${String.format("%,.0f", hotel.pricePerNight)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                    
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryBlue)
                    ) {
                        Text(text = "View Details", color = PrimaryBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, letterSpacing = 1.sp)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
    }
}

@Composable
fun AmenityIcon(icon: ImageVector, label: String) {
    Surface(
        modifier = Modifier.width(90.dp),
        shape = RoundedCornerShape(12.dp),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheetContent(onApply: () -> Unit, onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Advanced Filters", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            IconButton(
                onClick = onApply,
                modifier = Modifier.background(BackgroundLight, CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "PRICE RANGE (PER NIGHT)", style = MaterialTheme.typography.labelSmall, color = Color.Gray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PriceInput(label = "MIN", value = "$500", modifier = Modifier.weight(1f))
            PriceInput(label = "MAX", value = "$10,000+", modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "LUXURY EXPERIENCE", style = MaterialTheme.typography.labelSmall, color = Color.Gray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExperienceChip("Palace Class \u2728", true)
            ExperienceChip("Penthouse Suites", false)
            ExperienceChip("Boutique Manor", false)
            ExperienceChip("Private Villa", false)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(text = "Reset All", color = Color.Black)
            }
            PrimaryButton(
                text = "Apply Selected",
                onClick = onApply,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
fun PriceInput(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9FF)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

@Composable
fun ExperienceChip(text: String, selected: Boolean) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFFF9F9FF) else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f)) else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) PrimaryBlue else Color.Gray
        )
    }
}

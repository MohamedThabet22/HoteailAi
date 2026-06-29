package com.example.hoteailai.presentation.details

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.presentation.components.BeautifulProgressIndicator
import com.example.hoteailai.presentation.components.GoldButton
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@Composable
fun HotelDetailsScreen(
    onBackClick: () -> Unit,
    onBookNowClick: (String, Long, Int, Int) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BeautifulProgressIndicator()
        }
    }

    state.hotel?.let { hotel ->
        Scaffold(
            bottomBar = {
                BookingBottomBar(
                    price = hotel.pricePerNight,
                    onBookNowClick = { 
                        onBookNowClick(hotel.id, state.checkInDate, state.durationDays, state.guestCount) 
                    }
                )
            },
            containerColor = BackgroundLight
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.height(450.dp)) {
                    val pagerState = rememberPagerState(pageCount = { hotel.imageUrls.size })
                    
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = hotel.imageUrls[page],
                            contentDescription = hotel.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Pager Indicator for photos
                    if (hotel.imageUrls.size > 1) {
                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(hotel.imageUrls.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                val width = if (pagerState.currentPage == iteration) 18.dp else 6.dp
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .width(width)
                                        .height(6.dp)
                                        .animateContentSize()
                                )
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FloatingIconButton(icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onBackClick)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FloatingIconButton(icon = Icons.Default.Share, onClick = {})
                            FloatingIconButton(
                                icon = if (hotel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                onClick = { viewModel.toggleFavorite() },
                                tint = if (hotel.isFavorite) Color.Red else Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "PALACE DISTINCTION",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = hotel.name,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = hotel.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = hotel.location, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(16.dp))
                                    Text(text = "${hotel.rating}", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }
                                Text(text = "${hotel.reviewCount} REVIEWS", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            AmenityItem(Icons.Default.Spa, "Spa")
                            AmenityItem(Icons.Default.Pool, "Pool")
                            AmenityItem(Icons.Default.Restaurant, "Dining")
                            AmenityItem(Icons.Default.Wifi, "Wifi")
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        BookingPreferences(
                            checkInDate = state.checkInDate,
                            duration = state.durationDays,
                            guests = state.guestCount,
                            onDateChange = viewModel::onDateChange,
                            onDurationChange = viewModel::onDurationChange,
                            onGuestsChange = viewModel::onGuestCountChange
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(text = "EXPERIENCE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = hotel.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            lineHeight = 22.sp
                        )
                        Text(text = "Read more >", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(text = "LOCATION", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            val hotelLocation = LatLng(hotel.latitude, hotel.longitude)
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(hotelLocation, 15f)
                            }
                            
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = MapUiSettings(zoomControlsEnabled = false)
                            ) {
                                Marker(
                                    state = rememberMarkerState(position = hotelLocation),
                                    title = hotel.name,
                                    snippet = hotel.location
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "VERIFIED REVIEWS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Text(text = "View All", color = SecondaryGold, fontWeight = FontWeight.Medium)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ReviewCard()
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingIconButton(
    icon: ImageVector, 
    onClick: () -> Unit,
    tint: Color = Color.White
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.2f),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AmenityItem(icon: ImageVector, label: String) {
    Surface(
        modifier = Modifier.size(60.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFBFBFE)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPreferences(
    checkInDate: Long,
    duration: Int,
    guests: Int,
    onDateChange: (Long) -> Unit,
    onDurationChange: (Int) -> Unit,
    onGuestsChange: (Int) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = checkInDate)
    var showDatePicker by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "BOOKING DETAILS",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            letterSpacing = 2.sp
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Date Picker Trigger
            PreferenceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange,
                label = "Check-in",
                value = sdf.format(Date(checkInDate)),
                onClick = { showDatePicker = true }
            )

            // Duration Selector
            PreferenceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Nightlight,
                label = "Duration",
                value = "$duration Nights",
                onClick = { onDurationChange(duration + 1) }, // Simplified: just increment
                onSecondaryClick = { if (duration > 1) onDurationChange(duration - 1) }
            )
        }

        // Guest Selector
        PreferenceCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Group,
            label = "Guests",
            value = "$guests People",
            onClick = { onGuestsChange(guests + 1) },
            onSecondaryClick = { if (guests > 1) onGuestsChange(guests - 1) }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun PreferenceCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    onSecondaryClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = BackgroundLight,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
            if (onSecondaryClick != null) {
                IconButton(onClick = onSecondaryClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFBFBFE),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.LightGray) {}
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Elena G.", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text(text = "Stayed: Jan 2024", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "The level of service was truly beyond five stars. From the moment the doors opened, we...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun BookingBottomBar(price: Double, onBookNowClick: () -> Unit) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "$${price} /night", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Text(text = "Excl. taxes & fees", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            GoldButton(
                text = "Book Now",
                onClick = onBookNowClick,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

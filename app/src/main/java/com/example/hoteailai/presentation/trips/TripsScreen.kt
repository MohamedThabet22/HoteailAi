package com.example.hoteailai.presentation.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Booking
import com.example.hoteailai.presentation.components.BeautifulProgressIndicator
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    onBookingClick: (String) -> Unit,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MY REGAL TRIPS", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PrimaryBlue
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                BeautifulProgressIndicator()
            }
        } else if (state.bookings.isEmpty()) {
            EmptyTrips()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.bookings) { booking ->
                    TripItem(booking = booking, onClick = { onBookingClick(booking.id) })
                }
            }
        }
    }
}

@Composable
fun TripItem(booking: Booking, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = booking.hotelImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SecondaryGold.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = booking.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryGold
                    )
                }
                Text(
                    text = booking.hotelName,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Text(text = booking.location, color = Color.Gray, fontSize = 12.sp)
                }
                Text(
                    text = "${booking.checkInDate} - ${booking.checkOutDate}",
                    color = PrimaryBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.0f", booking.totalPrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun EmptyTrips() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No trips yet", color = Color.Gray)
        Text("Your future adventures will appear here", color = Color.Gray, fontSize = 12.sp)
    }
}

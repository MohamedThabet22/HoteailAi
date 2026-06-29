package com.example.hoteailai.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hoteailai.presentation.components.PrimaryButton
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@Composable
fun ConfirmationScreen(
    onBackToHome: () -> Unit,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Hotelio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            IconButton(onClick = onBackToHome) {
                Icon(Icons.Default.Close, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.05f)
            ) {}
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = PrimaryBlue
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Booking Confirmed", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(
            text = "Your regal stay is secured. A confirmation email has been sent.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        state.booking?.let { booking ->
            BookingDetailCard(
                imageUrl = booking.hotelImageUrl,
                hotelName = booking.hotelName,
                bookingId = "#HTL-992834", // Using a dummy for now
                checkIn = booking.checkInDate,
                location = booking.location
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "View My Trips \uD83D\uDCCB",
            onClick = onBackToHome
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Back to Home", color = PrimaryBlue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Back to Home",
            modifier = Modifier.clickable { onBackToHome() },
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BookingDetailCard(imageUrl: String, hotelName: String, bookingId: String, checkIn: String, location: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SecondaryGold
                ) {
                    Text(
                        text = "PREMIUM SUITE",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = hotelName,
                    modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "BOOKING ID", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = bookingId, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
                Column {
                    Text(text = "CHECK-IN", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = checkIn, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }
            
            Row(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = BackgroundLight) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryGold, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Location", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = location, fontSize = 12.sp, color = PrimaryBlue)
                }
            }
        }
    }
}

package com.example.hoteailai.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Hotel
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@Composable
fun HotelCard(
    hotel: Hotel, 
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(400.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = hotel.imageUrls.firstOrNull(),
                contentDescription = hotel.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Favorite Badge
            Surface(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.TopEnd)
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
            
            // Floating Info Card
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = hotel.name, 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.Bold, 
                                color = PrimaryBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, null, tint = SecondaryGold, modifier = Modifier.size(12.dp))
                                Text(
                                    text = hotel.location, 
                                    style = MaterialTheme.typography.bodySmall, 
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp), 
                            color = BackgroundLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), 
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, null, tint = SecondaryGold, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${hotel.rating}", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "PRICE STARTING", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 8.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$${hotel.pricePerNight.toInt()}", 
                                    style = MaterialTheme.typography.titleLarge, 
                                    fontWeight = FontWeight.Bold, 
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = "/night", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = Color.Gray, 
                                    modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                                )
                            }
                        }
                        
                        Button(
                            onClick = onClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Details", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

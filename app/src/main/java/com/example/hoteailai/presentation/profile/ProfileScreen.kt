package com.example.hoteailai.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.hoteailai.ui.theme.BackgroundLight
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.user == null && !state.isLoading) {
        onLogoutSuccess()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hotelio", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = state.user?.profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = SecondaryGold,
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.user?.name ?: "Anas Al-Attar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = SecondaryGold.copy(alpha = 0.1f)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SecondaryGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ELITE MEMBER", color = SecondaryGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(label = "Bookings", value = "12")
                StatItem(label = "Wishlist", value = "24")
                StatItem(label = "Points", value = "4.8k")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "ACCOUNT SETTINGS", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.fillMaxWidth())
                ProfileMenuItem(icon = Icons.Default.Person, title = "Personal Information")
                ProfileMenuItem(icon = Icons.Default.Payment, title = "Payment Methods")
                ProfileMenuItem(icon = Icons.Default.Notifications, title = "Notification Settings")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = "SUPPORT & LEGAL", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.fillMaxWidth())
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Help, title = "Help & Support")
                ProfileMenuItem(icon = Icons.Default.PrivacyTip, title = "Privacy Policy")
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Logout, title = "Logout", color = Color(0xFFC0392B), onClick = viewModel::logout)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Hotelio Premium v2.4.0", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Surface(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 18.sp)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {},
    color: Color = PrimaryBlue
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFBFBFE)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.05f)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = PrimaryBlue, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

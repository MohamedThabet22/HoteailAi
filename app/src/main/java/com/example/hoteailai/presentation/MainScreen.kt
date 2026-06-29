package com.example.hoteailai.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hoteailai.navigation.Screen
import com.example.hoteailai.presentation.home.HomeScreen
import com.example.hoteailai.presentation.profile.ProfileScreen
import com.example.hoteailai.presentation.search.SearchScreen
import com.example.hoteailai.presentation.trips.TripsScreen
import com.example.hoteailai.presentation.wishlist.WishlistScreen
import com.example.hoteailai.ui.theme.PrimaryBlue
import com.example.hoteailai.ui.theme.SecondaryGold

data class NavigationItem(
    val title: String,
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainScreen(
    onHotelClick: (String) -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        NavigationItem("Home", Screen.Home, Icons.Default.Home, Icons.Outlined.Home),
        NavigationItem("Saved", Screen.Wishlist, Icons.Default.Favorite, Icons.Default.FavoriteBorder),
        NavigationItem("Trips", Screen.Trips, Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Filled.List),
        NavigationItem("Profile", Screen.Profile, Icons.Default.Person, Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                            
                            CustomBottomNavItem(
                                item = item,
                                isSelected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        containerColor = com.example.hoteailai.ui.theme.BackgroundLight
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onHotelClick = onHotelClick,
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onHotelClick = onHotelClick
                )
            }
            composable(Screen.Wishlist.route) {
                WishlistScreen(onHotelClick = onHotelClick)
            }
            composable(Screen.Trips.route) {
                TripsScreen(
                    onBookingClick = { /* Handle booking details */ }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogoutSuccess = onLogoutSuccess
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryBlue else Color(0xFFB0B0C3),
        label = "color"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else Color.Transparent,
        label = "bg_color"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

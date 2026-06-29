package com.example.hoteailai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hoteailai.presentation.MainScreen
import com.example.hoteailai.presentation.auth.LoginScreen
import com.example.hoteailai.presentation.auth.RegisterScreen
import com.example.hoteailai.presentation.booking.CheckoutScreen
import com.example.hoteailai.presentation.booking.ConfirmationScreen
import com.example.hoteailai.presentation.details.HotelDetailsScreen
import com.example.hoteailai.presentation.onboarding.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = { isFirstTime, isLoggedIn ->
                    val destination = if (isLoggedIn) {
                        Screen.Home.route
                    } else if (isFirstTime) {
                        Screen.Login.route
                    } else {
                        Screen.Login.route // أو صفحة ترحيبية أخرى إذا رغبت
                    }

                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Home.route) {
            MainScreen(
                onHotelClick = { hotelId ->
                    navController.navigate(Screen.HotelDetails.createRoute(hotelId))
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.HotelDetails.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) {
            HotelDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onBookNowClick = { hotelId, checkIn, duration, guests ->
                    navController.navigate(Screen.Checkout.createRoute(hotelId, checkIn, duration, guests))
                }
            )
        }

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("checkIn") { type = NavType.LongType; defaultValue = 0L },
                navArgument("duration") { type = NavType.IntType; defaultValue = 1 },
                navArgument("guests") { type = NavType.IntType; defaultValue = 1 }
            )
        ) {
            CheckoutScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { bookingId ->
                    navController.navigate(Screen.Confirmation.createRoute(bookingId)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.Confirmation.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) {
            ConfirmationScreen(
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

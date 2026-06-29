package com.example.hoteailai.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hoteailai.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: (Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val alphaAnim = remember { Animatable(0f) }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(500)
            onNavigateNext(isLoggedIn!!)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Hotelio",
                style = MaterialTheme.typography.displayLarge,
                color = PrimaryBlue,
                modifier = Modifier.alpha(alphaAnim.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LUXURY STAYS",
                style = MaterialTheme.typography.labelLarge,
                color = PrimaryBlue.copy(alpha = 0.5f),
                modifier = Modifier.alpha(alphaAnim.value),
                letterSpacing = 4.sp
            )
        }
    }
}

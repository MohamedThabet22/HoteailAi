package com.example.hoteailai.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.hoteailai.R

@Composable
fun EmptyState(
    message: String,
    description: String? = null,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for Lottie
        // val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
        // LottieAnimation(composition, iterations = LottieConstants.IterateForever)
        
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = "Retry",
                onClick = onRetry
            )
        }
    }
}

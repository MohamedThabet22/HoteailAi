package com.example.hoteailai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGold,
    tertiary = LightGold,
    background = TextDark,
    surface = TextDark,
    onPrimary = SurfaceWhite,
    onSecondary = PrimaryBlue,
    onTertiary = PrimaryBlue,
    onBackground = SurfaceWhite,
    onSurface = SurfaceWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGold,
    tertiary = LightGold,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = PrimaryBlue,
    onTertiary = PrimaryBlue,
    onBackground = TextDark,
    onSurface = TextDark,
)

@Composable
fun HoteailAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as Activity
        activity.window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

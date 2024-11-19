package com.project.mpesatracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.ui.graphics.Color

// Light Theme Colors
private val md_theme_light_primary = Color(0xFF4CAF50) // Grass green
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
private val md_theme_light_primaryContainer = Color(0xFFB8E4B9)
private val md_theme_light_onPrimaryContainer = Color(0xFF002204)
private val md_theme_light_secondary = Color(0xFF52634F)
private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
private val md_theme_light_secondaryContainer = Color(0xFFD5E8CF)
private val md_theme_light_onSecondaryContainer = Color(0xFF101F0F)
private val md_theme_light_background = Color(0xFFFCFDF6)
private val md_theme_light_onBackground = Color(0xFF1A1C19)
private val md_theme_light_surface = Color(0xFFFCFDF6)
private val md_theme_light_onSurface = Color(0xFF1A1C19)

// Dark Theme Colors
private val md_theme_dark_primary = Color(0xFF9CD89F) // Lighter grass green for dark theme
private val md_theme_dark_onPrimary = Color(0xFF003909)
private val md_theme_dark_primaryContainer = Color(0xFF1B5E20)
private val md_theme_dark_onPrimaryContainer = Color(0xFFB8E4B9)
private val md_theme_dark_secondary = Color(0xFFB9CCB4)
private val md_theme_dark_onSecondary = Color(0xFF253423)
private val md_theme_dark_secondaryContainer = Color(0xFF3B4B38)
private val md_theme_dark_onSecondaryContainer = Color(0xFFD5E8CF)
private val md_theme_dark_background = Color(0xFF1A1C19)
private val md_theme_dark_onBackground = Color(0xFFE2E3DD)
private val md_theme_dark_surface = Color(0xFF1A1C19)
private val md_theme_dark_onSurface = Color(0xFFE2E3DD)

// ... rest of the existing code ...
private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
)

@Composable
fun MPesaTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 
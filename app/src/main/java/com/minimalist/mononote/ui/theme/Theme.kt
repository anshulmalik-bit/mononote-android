package com.minimalist.mononote.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = DarkTextPrimary,
    secondary = DarkTextSecondary,
    background = AmoledBlack,
    surface = AmoledBlack,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = AmoledBlack,
    onSecondary = AmoledBlack,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = AppleRedDark
)

private val LightColorScheme = lightColorScheme(
    primary = LightTextPrimary,
    secondary = LightTextSecondary,
    background = AppleWhite,
    surface = AppleWhite,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = AppleWhite,
    onSecondary = AppleWhite,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    error = AppleRed
)

@Composable
fun MononoteTheme(
    darkTheme: Boolean = false, // Defaults to Light Mode as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val bgColor = if (darkTheme) AmoledBlack else AppleWhite
            window.statusBarColor = bgColor.toArgb()
            window.navigationBarColor = bgColor.toArgb()
            
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

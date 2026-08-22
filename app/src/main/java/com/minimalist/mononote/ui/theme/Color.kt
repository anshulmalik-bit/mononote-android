package com.minimalist.mononote.ui.theme

import androidx.compose.ui.graphics.Color

// Light Mode Palette (Matches the exact iOS Mononote screenshot)
val IosLightBackground = Color(0xFFF2F2F5)
val IosLightCard = Color(0xFFFFFFFF)
val IosLightCardBorder = Color(0x0D000000)
val IosLightTextPrimary = Color(0xFF1C1C1E)
val IosLightTextPlaceholder = Color(0xFFA0A0A6)
val IosLightIcon = Color(0xFF8E8E93)
val IosLightPillBg = Color(0xFFFFFFFF)
val IosLightPillBorder = Color(0xFFE2E2E6)

// Dark Mode Palette (AMOLED)
val IosDarkBackground = Color(0xFF000000)
val IosDarkCard = Color(0xFF1C1C1E)
val IosDarkCardBorder = Color(0xFF2C2C2E)
val IosDarkTextPrimary = Color(0xFFF2F2F7)
val IosDarkTextPlaceholder = Color(0xFF636366)
val IosDarkIcon = Color(0xFF8E8E93)
val IosDarkPillBg = Color(0xFF1C1C1E)
val IosDarkPillBorder = Color(0xFF2C2C2E)

// Backward compatible aliases
val AmoledBlack = IosDarkBackground
val AppleWhite = IosLightBackground
val DarkSurface = IosDarkBackground
val LightSurface = IosLightBackground
val DarkSurfaceCard = IosDarkCard
val LightSurfaceCard = IosLightCard
val DarkSurfaceVariant = IosDarkCard
val LightSurfaceVariant = IosLightCard
val DarkBorder = IosDarkCardBorder
val LightBorder = IosLightCardBorder
val DarkTextPrimary = IosDarkTextPrimary
val LightTextPrimary = IosLightTextPrimary
val DarkTextSecondary = IosDarkIcon
val LightTextSecondary = IosLightIcon
val DarkTextDisabled = IosDarkTextPlaceholder
val LightTextDisabled = IosLightTextPlaceholder

// Accents
val AppleRed = Color(0xFFFF3B30)
val AppleRedDark = Color(0xFFFF453A)
val AppleRedGlow = Color(0x26FF3B30)
val AppleBlue = Color(0xFF007AFF)

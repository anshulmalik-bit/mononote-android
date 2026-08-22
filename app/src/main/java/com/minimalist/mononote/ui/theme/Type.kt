package com.minimalist.mononote.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getEditorTypography(fontStyle: String, fontSizeSp: Float): TextStyle {
    val family = when (fontStyle) {
        "mono" -> FontFamily.Monospace
        "serif" -> FontFamily.Serif
        else -> FontFamily.Default
    }

    return TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.55f).sp,
        letterSpacing = if (fontStyle == "mono") 0.sp else 0.2.sp,
        color = TextPrimaryDark
    )
}

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = TextPrimaryDark
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = TextPrimaryDark
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = TextSecondaryDark
    )
)

package com.minimalist.mononote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalist.mononote.ui.theme.AppleRed
import com.minimalist.mononote.ui.theme.DarkBorder
import com.minimalist.mononote.ui.theme.DarkSurfaceCard
import com.minimalist.mononote.ui.theme.DarkTextPrimary
import com.minimalist.mononote.ui.theme.DarkTextSecondary
import com.minimalist.mononote.ui.theme.LightBorder
import com.minimalist.mononote.ui.theme.LightSurfaceCard
import com.minimalist.mononote.ui.theme.LightTextPrimary
import com.minimalist.mononote.ui.theme.LightTextSecondary

@Composable
fun ActionToolbar(
    isLive: Boolean,
    isDark: Boolean,
    fontStyle: String,
    onToggleLive: () -> Unit,
    onToggleTheme: () -> Unit,
    onCycleFont: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onArchive: () -> Unit,
    onOpenArchiveHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconTint = if (isDark) DarkTextSecondary else LightTextSecondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔴 GO LIVE BUTTON (Apple iOS Capsule Style)
        GoLiveButton(
            isLive = isLive,
            isDark = isDark,
            onClick = onToggleLive
        )

        // RIGHT ACTION CONTROLS
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Typography Switcher (SANS / SERIF / MONO)
            FontBadge(
                fontStyle = fontStyle,
                isDark = isDark,
                onClick = onCycleFont
            )

            Spacer(modifier = Modifier.width(4.dp))

            // ☀️ / 🌙 Theme Toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Copy
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Share
            IconButton(
                onClick = onShare,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Archive & Clear Canvas (New Note)
            IconButton(
                onClick = onArchive,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = "Archive and Clear",
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Archive History Timeline
            IconButton(
                onClick = onOpenArchiveHistory,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Archive History",
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
fun GoLiveButton(
    isLive: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        isLive && isDark -> Color(0xFF2C1414)
        isLive && !isDark -> Color(0xFFFFECEB)
        !isLive && isDark -> DarkSurfaceCard
        else -> LightSurfaceCard
    }

    val border = when {
        isLive -> AppleRed
        isDark -> DarkBorder
        else -> LightBorder
    }

    val textColor = when {
        isLive -> AppleRed
        isDark -> DarkTextPrimary
        else -> LightTextPrimary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isLive) AppleRed else if (isDark) DarkTextSecondary else LightTextSecondary)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = if (isLive) "LIVE" else "Go Live",
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun FontBadge(
    fontStyle: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val label = when (fontStyle) {
        "mono" -> "MONO"
        "serif" -> "SERIF"
        else -> "SANS"
    }

    val bg = if (isDark) DarkSurfaceCard else LightSurfaceCard
    val border = if (isDark) DarkBorder else LightBorder
    val textColor = if (isDark) DarkTextSecondary else LightTextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

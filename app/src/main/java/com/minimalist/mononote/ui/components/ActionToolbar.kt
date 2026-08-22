package com.minimalist.mononote.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalist.mononote.ui.theme.AmoledBlack
import com.minimalist.mononote.ui.theme.LiveRed
import com.minimalist.mononote.ui.theme.SurfaceCard
import com.minimalist.mononote.ui.theme.SurfaceVariantDark
import com.minimalist.mononote.ui.theme.TextPrimaryDark
import com.minimalist.mononote.ui.theme.TextSecondaryDark

@Composable
fun ActionToolbar(
    isLive: Boolean,
    fontStyle: String,
    onToggleLive: () -> Unit,
    onCycleFont: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onArchive: () -> Unit,
    onOpenArchiveHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔴 GO LIVE BUTTON (With Live Indicator)
        GoLiveButton(
            isLive = isLive,
            onClick = onToggleLive
        )

        // RIGHT ACTION ICONS
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Font Switcher (Sans / Mono / Serif)
            FontBadge(
                fontStyle = fontStyle,
                onClick = onCycleFont
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Copy
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Share
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Archive Current Note & Start Fresh
            IconButton(onClick = onArchive) {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = "Archive and Clear",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            // View Past History
            IconButton(onClick = onOpenArchiveHistory) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Archive History",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GoLiveButton(
    isLive: Boolean,
    onClick: () -> Unit
) {
    val liveBgColor = if (isLive) Color(0xFF2A1010) else SurfaceCard
    val liveBorderColor = if (isLive) LiveRed else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(liveBgColor)
            .border(1.dp, liveBorderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isLive) LiveRed else TextSecondaryDark)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = if (isLive) "LIVE" else "Go Live",
                color = if (isLive) LiveRed else TextPrimaryDark,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun FontBadge(
    fontStyle: String,
    onClick: () -> Unit
) {
    val label = when (fontStyle) {
        "mono" -> "MONO"
        "serif" -> "SERIF"
        else -> "SANS"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextSecondaryDark,
            fontSize = 11.sp,
            letterSpacing = 0.5.sp
        )
    }
}

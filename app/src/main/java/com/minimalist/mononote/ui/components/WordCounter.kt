package com.minimalist.mononote.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalist.mononote.ui.theme.TextSecondaryDark

@Composable
fun WordCounter(
    text: String,
    modifier: Modifier = Modifier
) {
    val words = if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    val chars = text.length

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$words words",
            color = TextSecondaryDark,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "•",
            color = TextSecondaryDark,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$chars chars",
            color = TextSecondaryDark,
            fontSize = 12.sp
        )
    }
}

package com.minimalist.mononote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalist.mononote.data.NoteEntity
import com.minimalist.mononote.ui.theme.IosDarkBackground
import com.minimalist.mononote.ui.theme.IosDarkCard
import com.minimalist.mononote.ui.theme.IosDarkCardBorder
import com.minimalist.mononote.ui.theme.IosDarkIcon
import com.minimalist.mononote.ui.theme.IosDarkTextPlaceholder
import com.minimalist.mononote.ui.theme.IosDarkTextPrimary
import com.minimalist.mononote.ui.theme.IosLightBackground
import com.minimalist.mononote.ui.theme.IosLightCard
import com.minimalist.mononote.ui.theme.IosLightCardBorder
import com.minimalist.mononote.ui.theme.IosLightIcon
import com.minimalist.mononote.ui.theme.IosLightTextPlaceholder
import com.minimalist.mononote.ui.theme.IosLightTextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveSheet(
    archivedNotes: List<NoteEntity>,
    isDark: Boolean,
    onRestore: (Long) -> Unit,
    onCopy: (String) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetBg = if (isDark) IosDarkBackground else IosLightBackground
    val textPrimary = if (isDark) IosDarkTextPrimary else IosLightTextPrimary
    val textSecondary = if (isDark) IosDarkIcon else IosLightIcon
    val textDisabled = if (isDark) IosDarkTextPlaceholder else IosLightTextPlaceholder

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        contentColor = textPrimary,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Archive Timeline",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "${archivedNotes.size} notes",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (archivedNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No archived notes yet.\nWhen you clear your canvas, old notes appear here.",
                        color = textDisabled,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(archivedNotes, key = { it.id }) { note ->
                        ArchivedNoteCard(
                            note = note,
                            isDark = isDark,
                            onRestore = { onRestore(note.id) },
                            onCopy = { onCopy(note.content) },
                            onDelete = { onDelete(note.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ArchivedNoteCard(
    note: NoteEntity,
    isDark: Boolean,
    onRestore: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(note.updatedAt))
    val bg = if (isDark) IosDarkCard else IosLightCard
    val border = if (isDark) IosDarkCardBorder else IosLightCardBorder
    val textPrimary = if (isDark) IosDarkTextPrimary else IosLightTextPrimary
    val textSecondary = if (isDark) IosDarkIcon else IosLightIcon
    val textDisabled = if (isDark) IosDarkTextPlaceholder else IosLightTextPlaceholder

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = dateStr,
                fontSize = 11.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content.ifBlank { "(Empty Note)" },
                fontSize = 14.sp,
                color = textPrimary,
                maxLines = 4,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Restore Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onRestore() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore",
                        tint = textSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Restore",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Copy Button
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = textSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = textDisabled,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

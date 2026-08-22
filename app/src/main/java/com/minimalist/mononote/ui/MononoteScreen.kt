package com.minimalist.mononote.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minimalist.mononote.ui.components.ActionToolbar
import com.minimalist.mononote.ui.components.WordCounter
import com.minimalist.mononote.ui.theme.AmoledBlack
import com.minimalist.mononote.ui.theme.TextDisabledDark
import com.minimalist.mononote.ui.theme.TextPrimaryDark
import com.minimalist.mononote.ui.theme.getEditorTypography

@Composable
fun MononoteScreen(
    viewModel: MononoteViewModel
) {
    val context = LocalContext.current
    val activeNote by viewModel.activeNote.collectAsState()
    val archivedNotes by viewModel.archivedNotes.collectAsState()
    val showArchiveSheet by viewModel.showArchiveSheet.collectAsState()

    var localContent by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    // Sync local state with database
    LaunchedEffect(activeNote.id, activeNote.content) {
        if (localContent != activeNote.content) {
            localContent = activeNote.content
        }
    }

    // Auto-focus keyboard on launch
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    Scaffold(
        containerColor = AmoledBlack,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AmoledBlack)
        ) {
            // TOP HEADER: Word Counter & Live Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WordCounter(text = localContent)
                Spacer(modifier = Modifier.weight(1f))
            }

            // MAIN SINGLE-NOTE CANVAS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                if (localContent.isEmpty()) {
                    Text(
                        text = "Capture a thought, daily focus, or task list...\n\nTap \"Go Live\" to pin this note to your lock screen & status bar.",
                        style = getEditorTypography(activeNote.fontStyle, activeNote.fontSize).copy(
                            color = TextDisabledDark
                        )
                    )
                }

                BasicTextField(
                    value = localContent,
                    onValueChange = { newText ->
                        localContent = newText
                        viewModel.onContentChange(newText)
                    },
                    textStyle = getEditorTypography(activeNote.fontStyle, activeNote.fontSize),
                    cursorBrush = SolidColor(TextPrimaryDark),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                )
            }

            // BOTTOM ACTION TOOLBAR
            ActionToolbar(
                isLive = activeNote.isLive,
                fontStyle = activeNote.fontStyle,
                onToggleLive = { viewModel.toggleLiveStatus() },
                onCycleFont = { viewModel.cycleFontStyle() },
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Mononote", localContent))
                    Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    if (localContent.isNotBlank()) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, localContent)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                    }
                },
                onArchive = {
                    if (localContent.isNotBlank()) {
                        viewModel.archiveAndClear()
                        Toast.makeText(context, "Note archived • Fresh canvas ready ✨", Toast.LENGTH_SHORT).show()
                    }
                },
                onOpenArchiveHistory = {
                    viewModel.setArchiveSheetVisible(true)
                }
            )
        }
    }

    // ARCHIVE TIMELINE MODAL SHEET
    if (showArchiveSheet) {
        ArchiveSheet(
            archivedNotes = archivedNotes,
            onRestore = { id -> viewModel.restoreArchivedNote(id) },
            onCopy = { text ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Mononote", text))
                Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
            },
            onDelete = { id -> viewModel.deleteArchivedNote(id) },
            onDismiss = { viewModel.setArchiveSheetVisible(false) }
        )
    }
}

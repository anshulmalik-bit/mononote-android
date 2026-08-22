package com.minimalist.mononote.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.minimalist.mononote.ui.components.ActionToolbar
import com.minimalist.mononote.ui.components.rememberVoiceInput
import com.minimalist.mononote.ui.theme.AmoledBlack
import com.minimalist.mononote.ui.theme.AppleBlue
import com.minimalist.mononote.ui.theme.AppleRed
import com.minimalist.mononote.ui.theme.AppleWhite
import com.minimalist.mononote.ui.theme.DarkBorder
import com.minimalist.mononote.ui.theme.DarkSurfaceCard
import com.minimalist.mononote.ui.theme.DarkTextDisabled
import com.minimalist.mononote.ui.theme.DarkTextPrimary
import com.minimalist.mononote.ui.theme.LightBorder
import com.minimalist.mononote.ui.theme.LightSurfaceCard
import com.minimalist.mononote.ui.theme.LightTextDisabled
import com.minimalist.mononote.ui.theme.LightTextPrimary
import com.minimalist.mononote.ui.theme.getEditorTypography

@Composable
fun MononoteScreen(
    viewModel: MononoteViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val activeNote by viewModel.activeNote.collectAsState()
    val archivedNotes by viewModel.archivedNotes.collectAsState()
    val showArchiveSheet by viewModel.showArchiveSheet.collectAsState()

    var localContent by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    val screenBg = if (isDark) AmoledBlack else AppleWhite
    val textPrimary = if (isDark) DarkTextPrimary else LightTextPrimary
    val textDisabled = if (isDark) DarkTextDisabled else LightTextDisabled
    val cursorColor = if (isDark) DarkTextPrimary else AppleBlue

    // Voice Dictation Manager
    val voiceInputState = rememberVoiceInput(
        onResult = { spokenText ->
            val updated = if (localContent.isBlank()) {
                spokenText
            } else {
                "$localContent\n• $spokenText"
            }
            localContent = updated
            viewModel.onContentChange(updated)
            Toast.makeText(context, "Voice added ✨", Toast.LENGTH_SHORT).show()
        },
        onError = { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    )

    // Permission launcher for microphone
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceInputState.startListening()
        } else {
            Toast.makeText(context, "Microphone permission required for voice dictation", Toast.LENGTH_SHORT).show()
        }
    }

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
        containerColor = screenBg,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(screenBg)
        ) {
            // TOP LISTENING BANNER (Subtle animation when voice is active)
            AnimatedVisibility(
                visible = voiceInputState.isListening,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) DarkSurfaceCard else LightSurfaceCard)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🔴",
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Listening... Speak now",
                                fontSize = 12.sp,
                                color = if (isDark) DarkTextPrimary else LightTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // MAIN SINGLE-NOTE CANVAS (Pure distraction-free Apple iOS paper canvas)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                if (localContent.isEmpty()) {
                    Text(
                        text = "Write anything...",
                        style = getEditorTypography(activeNote.fontStyle, activeNote.fontSize, isDark).copy(
                            color = textDisabled
                        )
                    )
                }

                BasicTextField(
                    value = localContent,
                    onValueChange = { newText ->
                        localContent = newText
                        viewModel.onContentChange(newText)
                    },
                    textStyle = getEditorTypography(activeNote.fontStyle, activeNote.fontSize, isDark),
                    cursorBrush = SolidColor(cursorColor),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                )
            }

            // BOTTOM ACTION TOOLBAR
            ActionToolbar(
                isLive = activeNote.isLive,
                isDark = isDark,
                isListening = voiceInputState.isListening,
                fontStyle = activeNote.fontStyle,
                onToggleLive = { viewModel.toggleLiveStatus() },
                onToggleVoice = {
                    if (voiceInputState.isListening) {
                        voiceInputState.stopListening()
                    } else {
                        val hasMicPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasMicPerm) {
                            voiceInputState.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onToggleTheme = onToggleTheme,
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
            isDark = isDark,
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

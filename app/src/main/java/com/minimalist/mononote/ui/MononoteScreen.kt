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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.minimalist.mononote.ui.components.rememberVoiceInput
import com.minimalist.mononote.ui.theme.AppleBlue
import com.minimalist.mononote.ui.theme.AppleRed
import com.minimalist.mononote.ui.theme.IosDarkBackground
import com.minimalist.mononote.ui.theme.IosDarkCard
import com.minimalist.mononote.ui.theme.IosDarkCardBorder
import com.minimalist.mononote.ui.theme.IosDarkIcon
import com.minimalist.mononote.ui.theme.IosDarkPillBg
import com.minimalist.mononote.ui.theme.IosDarkPillBorder
import com.minimalist.mononote.ui.theme.IosDarkTextPlaceholder
import com.minimalist.mononote.ui.theme.IosDarkTextPrimary
import com.minimalist.mononote.ui.theme.IosLightBackground
import com.minimalist.mononote.ui.theme.IosLightCard
import com.minimalist.mononote.ui.theme.IosLightCardBorder
import com.minimalist.mononote.ui.theme.IosLightIcon
import com.minimalist.mononote.ui.theme.IosLightPillBg
import com.minimalist.mononote.ui.theme.IosLightPillBorder
import com.minimalist.mononote.ui.theme.IosLightTextPlaceholder
import com.minimalist.mononote.ui.theme.IosLightTextPrimary

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
    var showMenu by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    // Color tokens matching the exact iOS Mononote screenshot
    val screenBg = if (isDark) IosDarkBackground else IosLightBackground
    val cardBg = if (isDark) IosDarkCard else IosLightCard
    val cardBorder = if (isDark) IosDarkCardBorder else IosLightCardBorder
    val textPrimary = if (isDark) IosDarkTextPrimary else IosLightTextPrimary
    val textPlaceholder = if (isDark) IosDarkTextPlaceholder else IosLightTextPlaceholder
    val iconColor = if (isDark) IosDarkIcon else IosLightIcon
    val pillBg = if (isDark) IosDarkPillBg else IosLightPillBg
    val pillBorder = if (isDark) IosDarkPillBorder else IosLightPillBorder
    val cursorColor = if (isDark) IosDarkTextPrimary else AppleBlue

    // Voice Dictation Manager
    val voiceInputState = rememberVoiceInput(
        onResult = { spokenText ->
            val updated = if (localContent.isBlank()) {
                spokenText
            } else {
                "$localContent\n$spokenText"
            }
            localContent = updated
            viewModel.onContentChange(updated)
            Toast.makeText(context, "Voice note added ✨", Toast.LENGTH_SHORT).show()
        },
        onError = { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    )

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
                .background(screenBg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ─────────────────────────────────────────────────────────────
            // 1. TOP BAR (Share • Mononote • ...)
            // ─────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Share Icon
                IconButton(
                    onClick = {
                        if (localContent.isNotBlank()) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, localContent)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = "Share",
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Center: Mononote Title
                Text(
                    text = "Mononote",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                    letterSpacing = (-0.2).sp
                )

                // Right: ... (More Menu)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(cardBg)
                    ) {
                        // 🔤 Font Style
                        DropdownMenuItem(
                            text = {
                                val fontName = when (activeNote.fontStyle) {
                                    "mono" -> "Font: Monospace"
                                    "serif" -> "Font: Serif"
                                    else -> "Font: Sans"
                                }
                                Text(
                                    text = fontName,
                                    color = textPrimary,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                showMenu = false
                                viewModel.cycleFontStyle()
                            }
                        )

                        // ☀️ / 🌙 Theme Toggle
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isDark) "☀️ Light Mode" else "🌙 Dark Mode",
                                    color = textPrimary,
                                    fontSize = 14.sp
                                )
                            },
                            onClick = {
                                showMenu = false
                                onToggleTheme()
                            }
                        )

                        // 📋 Copy All
                        DropdownMenuItem(
                            text = { Text("📋 Copy All", color = textPrimary, fontSize = 14.sp) },
                            onClick = {
                                showMenu = false
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Mononote", localContent))
                                Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 2. CENTER HERO FLOATING STICKY CARD
            // ─────────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 340.dp, max = 460.dp)
                        .shadow(
                            elevation = if (isDark) 0.dp else 10.dp,
                            shape = RoundedCornerShape(26.dp),
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x10000000)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(26.dp))
                        .padding(24.dp)
                ) {
                    if (localContent.isEmpty()) {
                        Text(
                            text = if (voiceInputState.isListening) "Listening... Speak now 🎙️" else "Start typing...",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = if (voiceInputState.isListening) AppleRed else textPlaceholder
                            )
                        )
                    }

                    BasicTextField(
                        value = localContent,
                        onValueChange = { newText ->
                            localContent = newText
                            viewModel.onContentChange(newText)
                        },
                        textStyle = TextStyle(
                            fontFamily = when (activeNote.fontStyle) {
                                "sans" -> FontFamily.Default
                                "serif" -> FontFamily.Serif
                                else -> FontFamily.Monospace
                            },
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = textPrimary
                        ),
                        cursorBrush = SolidColor(cursorColor),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .focusRequester(focusRequester)
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────
            // 3. BOTTOM BAR (Archive Box • 🎙️ Mic • ⭕ Go Live • 🗑️ Delete)
            // ─────────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Left: Archive Box Icon
                IconButton(
                    onClick = { viewModel.setArchiveSheetVisible(true) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = "Archive Timeline",
                        tint = iconColor,
                        modifier = Modifier.size(21.dp)
                    )
                }

                // 2. 🎙️ 1-TAP ON-SCREEN VOICE MIC BUTTON
                val micBg = if (voiceInputState.isListening) Color(0xFFFFECEB) else pillBg
                val micBorder = if (voiceInputState.isListening) AppleRed else pillBorder
                val micTint = if (voiceInputState.isListening) AppleRed else iconColor

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(micBg)
                        .border(1.dp, micBorder, CircleShape)
                        .clickable {
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
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (voiceInputState.isListening) Icons.Default.MicOff else Icons.Outlined.Mic,
                        contentDescription = "Voice Dictation",
                        tint = micTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 3. Center: ⭕ Go Live Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (activeNote.isLive) Color(0xFFFFECEB) else pillBg)
                        .border(
                            1.dp,
                            if (activeNote.isLive) AppleRed else pillBorder,
                            RoundedCornerShape(22.dp)
                        )
                        .clickable { viewModel.toggleLiveStatus() }
                        .padding(horizontal = 18.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (activeNote.isLive) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(AppleRed)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .border(1.5.dp, iconColor, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(7.dp))

                        Text(
                            text = if (activeNote.isLive) "LIVE" else "Go Live",
                            color = if (activeNote.isLive) AppleRed else textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp
                        )
                    }
                }

                // 4. Right: Trash / Delete Icon (Archives & Clears Canvas)
                IconButton(
                    onClick = {
                        if (localContent.isNotBlank()) {
                            viewModel.archiveAndClear()
                            Toast.makeText(context, "Note archived • Canvas cleared ✨", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete / Clear",
                        tint = iconColor,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
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

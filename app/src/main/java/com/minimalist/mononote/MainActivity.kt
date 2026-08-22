package com.minimalist.mononote

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.minimalist.mononote.ui.MononoteScreen
import com.minimalist.mononote.ui.MononoteViewModel
import com.minimalist.mononote.ui.MononoteViewModelFactory
import com.minimalist.mononote.ui.theme.MononoteTheme

class MainActivity : ComponentActivity() {

    private val app by lazy { application as MononoteApplication }
    private val viewModel: MononoteViewModel by viewModels {
        MononoteViewModelFactory(app.repository, app.liveCardManager)
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val note = viewModel.activeNote.value
                if (note.isLive) {
                    app.liveCardManager.updateLiveCard(note)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable full Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Request notification permission for Android 13+ (Required for Live Card)
        checkNotificationPermission()

        // Handle incoming text / voice note intents
        handleIncomingIntent(intent)

        setContent {
            // Light Mode by default (exactly like iOS default)
            var isDarkTheme by remember { mutableStateOf(false) }

            MononoteTheme(darkTheme = isDarkTheme) {
                MononoteScreen(
                    viewModel = viewModel,
                    isDark = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val isShare = intent.action == Intent.ACTION_SEND && intent.type == "text/plain"
        val isVoiceNote = intent.action == "com.google.android.gms.actions.CREATE_NOTE" ||
                intent.action == "android.intent.action.CREATE_NOTE"

        if (isShare || isVoiceNote) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                ?: intent.getStringExtra(Intent.EXTRA_TITLE)
                ?: intent.getStringExtra(Intent.EXTRA_SUBJECT)
                ?: intent.getStringExtra("android.intent.extra.TEXT")

            if (!text.isNullOrBlank()) {
                viewModel.appendVoiceNote(text)
            }
        }
    }
}

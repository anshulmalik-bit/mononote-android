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
                // If the note was marked live, refresh notification
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

        // Handle shared text into Mononote
        handleIncomingIntent(intent)

        setContent {
            MononoteTheme {
                MononoteScreen(viewModel = viewModel)
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
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                val current = viewModel.activeNote.value.content
                val updated = if (current.isBlank()) sharedText else "$current\n\n$sharedText"
                viewModel.onContentChange(updated)
            }
        }
    }
}

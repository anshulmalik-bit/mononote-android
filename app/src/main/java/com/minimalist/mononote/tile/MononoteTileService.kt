package com.minimalist.mononote.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.MononoteApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MononoteTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val app = application as MononoteApplication
        CoroutineScope(Dispatchers.Main).launch {
            val note = app.repository.getActiveNoteDirect()
            qsTile?.apply {
                state = if (note.isLive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                label = if (note.content.isBlank()) "Mononote" else note.content.lines().first().take(18)
                updateTile()
            }
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ startActivityAndCollapse
            startActivityAndCollapse(
                android.app.PendingIntent.getActivity(
                    this, 0, intent,
                    android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}

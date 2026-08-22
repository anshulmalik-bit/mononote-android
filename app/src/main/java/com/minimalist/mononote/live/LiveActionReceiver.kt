package com.minimalist.mononote.live

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.minimalist.mononote.MononoteApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as MononoteApplication
        val repository = app.repository
        val liveManager = app.liveCardManager

        when (intent?.action) {
            LiveCardManager.ACTION_COMPLETE_NOTE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.archiveActiveNote()
                    liveManager.dismissLiveCard()
                }
                Toast.makeText(context, "Note completed & archived ✓", Toast.LENGTH_SHORT).show()
            }
            LiveCardManager.ACTION_COPY_NOTE -> {
                val text = intent.getStringExtra("content") ?: ""
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Mononote", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
            }
            LiveCardManager.ACTION_STOP_LIVE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.setLiveStatus(false)
                    liveManager.dismissLiveCard()
                }
            }
        }
    }
}

package com.minimalist.mononote

import android.app.Application
import com.minimalist.mononote.data.AppDatabase
import com.minimalist.mononote.data.NoteRepository
import com.minimalist.mononote.live.LiveCardManager

class MononoteApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
    val liveCardManager by lazy { LiveCardManager(this, repository) }

    override fun onCreate() {
        super.onCreate()
        liveCardManager.createNotificationChannel()
    }
}

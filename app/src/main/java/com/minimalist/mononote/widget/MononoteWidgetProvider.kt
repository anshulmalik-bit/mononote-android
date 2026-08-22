package com.minimalist.mononote.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.R
import com.minimalist.mononote.data.AppDatabase
import com.minimalist.mononote.data.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Mononote4x4WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAllWidgets(context)
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(ComponentName(context, Mononote4x4WidgetProvider::class.java))

                withContext(Dispatchers.Main) {
                    for (appWidgetId in ids) {
                        val views = RemoteViews(context.packageName, R.layout.widget_note_4x4)
                        val text = note.content.ifBlank { "Start typing..." }
                        views.setTextViewText(R.id.widget_content, text)

                        if (note.isLive) {
                            views.setViewVisibility(R.id.widget_live_badge, View.VISIBLE)
                        } else {
                            views.setViewVisibility(R.id.widget_live_badge, View.GONE)
                        }

                        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(note.updatedAt))
                        views.setTextViewText(R.id.widget_time, timeStr)

                        // 1-Tap opens Mononote
                        val openIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId,
                            openIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                        manager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }
}

class Mononote2x2WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllWidgets(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAllWidgets(context)
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(ComponentName(context, Mononote2x2WidgetProvider::class.java))

                withContext(Dispatchers.Main) {
                    for (appWidgetId in ids) {
                        val views = RemoteViews(context.packageName, R.layout.widget_note_2x2)
                        val text = note.content.ifBlank { "Start typing..." }
                        views.setTextViewText(R.id.widget_content, text)

                        if (note.isLive) {
                            views.setViewVisibility(R.id.widget_live_badge, View.VISIBLE)
                        } else {
                            views.setViewVisibility(R.id.widget_live_badge, View.GONE)
                        }

                        // 1-Tap opens Mononote
                        val openIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            appWidgetId,
                            openIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                        manager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }
}

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

open class Mononote4x4WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAll(context)
    }

    companion object {
        fun updateAll(context: Context) {
            updateAllWidgets(context)
        }

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, Mononote4x4WidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                updateWidgets(context, manager, ids)
            }
        }

        private fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
                val text = note.content.ifBlank { "Start typing..." }
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(note.updatedAt))

                withContext(Dispatchers.Main) {
                    for (appWidgetId in appWidgetIds) {
                        val views = RemoteViews(context.packageName, R.layout.widget_note_4x4)
                        views.setTextViewText(R.id.widget_content, text)
                        views.setTextViewText(R.id.widget_time, timeStr)
                        views.setViewVisibility(
                            R.id.widget_live_badge,
                            if (note.isLive) View.VISIBLE else View.GONE
                        )

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

                        try {
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }
}

open class Mononote2x2WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        updateAll(context)
    }

    companion object {
        fun updateAll(context: Context) {
            updateAllWidgets(context)
        }

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, Mononote2x2WidgetProvider::class.java))
            if (ids.isNotEmpty()) {
                updateWidgets(context, manager, ids)
            }
        }

        private fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
                val text = note.content.ifBlank { "Start typing..." }

                withContext(Dispatchers.Main) {
                    for (appWidgetId in appWidgetIds) {
                        val views = RemoteViews(context.packageName, R.layout.widget_note_2x2)
                        views.setTextViewText(R.id.widget_content, text)
                        views.setViewVisibility(
                            R.id.widget_live_badge,
                            if (note.isLive) View.VISIBLE else View.GONE
                        )

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

                        try {
                            appWidgetManager.updateAppWidget(appWidgetId, views)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }
}

class MononoteWidgetReceiver : Mononote4x4WidgetProvider()

package com.minimalist.mononote.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.R
import com.minimalist.mononote.data.AppDatabase
import com.minimalist.mononote.data.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MononoteWidget"

open class Mononote4x4WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "4x4 onUpdate called, ids=${appWidgetIds.toList()}")
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                performUpdate(context, appWidgetManager, appWidgetIds)
            } catch (e: Exception) {
                Log.e(TAG, "4x4 onUpdate error", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        fun updateAll(context: Context) {
            updateAllWidgets(context)
        }

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val component4x4 = ComponentName(context, Mononote4x4WidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component4x4)
            Log.d(TAG, "updateAllWidgets 4x4: ids=${ids.toList()}")
            if (ids.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        performUpdate(context, manager, ids)
                    } catch (e: Exception) {
                        Log.e(TAG, "updateAllWidgets 4x4 error", e)
                    }
                }
            }
            // Also update legacy receiver widgets
            val componentLegacy = ComponentName(context, MononoteWidgetReceiver::class.java)
            val legacyIds = manager.getAppWidgetIds(componentLegacy)
            if (legacyIds.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        performUpdate(context, manager, legacyIds)
                    } catch (e: Exception) {
                        Log.e(TAG, "updateAllWidgets legacy error", e)
                    }
                }
            }
        }

        private suspend fun performUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val db = AppDatabase.getDatabase(context)
            val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
            val text = note.content.ifBlank { "Start typing..." }
            val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(note.updatedAt))

            Log.d(TAG, "4x4 performUpdate: text='${text.take(30)}', ids=${appWidgetIds.toList()}")

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
                    Log.d(TAG, "4x4 updated appWidgetId=$appWidgetId OK")
                } catch (e: Exception) {
                    Log.e(TAG, "4x4 updateAppWidget failed for id=$appWidgetId", e)
                }
            }
        }
    }
}

open class Mononote2x2WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "2x2 onUpdate called, ids=${appWidgetIds.toList()}")
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                performUpdate(context, appWidgetManager, appWidgetIds)
            } catch (e: Exception) {
                Log.e(TAG, "2x2 onUpdate error", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        fun updateAll(context: Context) {
            updateAllWidgets(context)
        }

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context) ?: return
            val ids = manager.getAppWidgetIds(ComponentName(context, Mononote2x2WidgetProvider::class.java))
            Log.d(TAG, "updateAllWidgets 2x2: ids=${ids.toList()}")
            if (ids.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        performUpdate(context, manager, ids)
                    } catch (e: Exception) {
                        Log.e(TAG, "updateAllWidgets 2x2 error", e)
                    }
                }
            }
        }

        private suspend fun performUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val db = AppDatabase.getDatabase(context)
            val note = db.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
            val text = note.content.ifBlank { "Start typing..." }

            Log.d(TAG, "2x2 performUpdate: text='${text.take(30)}', ids=${appWidgetIds.toList()}")

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
                    Log.d(TAG, "2x2 updated appWidgetId=$appWidgetId OK")
                } catch (e: Exception) {
                    Log.e(TAG, "2x2 updateAppWidget failed for id=$appWidgetId", e)
                }
            }
        }
    }
}

class MononoteWidgetReceiver : Mononote4x4WidgetProvider()

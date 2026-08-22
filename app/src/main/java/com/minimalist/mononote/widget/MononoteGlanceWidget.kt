package com.minimalist.mononote.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.R
import com.minimalist.mononote.data.AppDatabase
import com.minimalist.mononote.data.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MononoteGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = AppDatabase.getDatabase(context)
        val note = withContext(Dispatchers.IO) {
            database.noteDao().getActiveNoteDirect() ?: NoteEntity(isActive = true)
        }

        provideContent {
            GlanceTheme {
                WidgetContent(note)
            }
        }
    }

    @Composable
    private fun WidgetContent(note: NoteEntity) {
        val content = note.content.ifBlank { "Tap to capture a focus or thought..." }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(0xFF0A0A0A.toInt()))
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (note.isLive) {
                        Text(
                            text = "🔴 LIVE",
                            style = TextStyle(
                                color = ColorProvider(0xFFFF453A.toInt()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.padding(horizontal = 4.dp))
                    }
                    Text(
                        text = "MONONOTE",
                        style = TextStyle(
                            color = ColorProvider(0xFF888888.toInt()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = content,
                    maxLines = 6,
                    style = TextStyle(
                        color = ColorProvider(0xFFEEEEEE.toInt()),
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

class MononoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MononoteGlanceWidget()
}

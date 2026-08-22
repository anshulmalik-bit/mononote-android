package com.minimalist.mononote.live

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.minimalist.mononote.MainActivity
import com.minimalist.mononote.MononoteApplication
import com.minimalist.mononote.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FloatingCapsuleService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 80 // Just below camera cutout on Redmi Note 11
        }

        // Programmatic sleek capsule view
        val capsuleTextView = TextView(this).apply {
            text = "🔴 Mononote"
            setTextColor(0xFFEEEEEE.toInt())
            textSize = 12f
            setPadding(36, 16, 36, 16)
            setBackgroundColor(0xFF000000.toInt())
            elevation = 16f
            setOnClickListener {
                val intent = Intent(this@FloatingCapsuleService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
            }
        }

        floatingView = capsuleTextView
        windowManager?.addView(floatingView, params)

        val app = application as MononoteApplication
        job = CoroutineScope(Dispatchers.Main).launch {
            app.repository.activeNoteFlow.collectLatest { note ->
                if (!note.isLive) {
                    stopSelf()
                } else {
                    val preview = note.content.lines().firstOrNull()?.take(28) ?: "Mononote Live"
                    capsuleTextView.text = "🔴 $preview"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }
}

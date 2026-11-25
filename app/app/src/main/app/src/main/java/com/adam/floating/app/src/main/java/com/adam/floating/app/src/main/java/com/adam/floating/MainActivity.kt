package com.adam.floating

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var coreEngine: AdamCoreEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coreEngine = AdamCoreEngine(this)

        statusText = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 36)
        }

        val startButton = Button(this).apply {
            text = "Start Adam Overlay"
            setOnClickListener { ensureOverlayPermissionAndStart() }
        }

        val stopButton = Button(this).apply {
            text = "Stop Adam Overlay"
            setOnClickListener {
                stopService(Intent(this@MainActivity, FloatingOverlayService::class.java))
                refreshStatus()
            }
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(statusText)
            addView(startButton)
            addView(stopButton)
        }

        setContentView(root)
        refreshStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun ensureOverlayPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            startService(Intent(this, FloatingOverlayService::class.java))
            refreshStatus()
        }
    }

    private fun refreshStatus() {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
        val permLabel = if (hasPermission) "Granted" else "Required"
        statusText.text = "Adam Mood: ${coreEngine.currentMood}\nOverlay Permission: $permLabel"
    }
}

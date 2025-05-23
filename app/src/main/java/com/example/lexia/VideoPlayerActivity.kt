package com.example.lexia

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_player)
        hideSystemUI()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val webView = findViewById<WebView>(R.id.webView)

        // Get the video URL passed from the previous activity
        val videoId = intent.getStringExtra("videoId")

        // Enable JavaScript (required for Vimeo embeds)
        webView.settings.javaScriptEnabled = true

        // Load the Vimeo video
        if (videoId != null) {
            val videoUrl = "https://player.vimeo.com/video/$videoId"
            webView.loadUrl(videoUrl)
        } else {
            Toast.makeText(this, "Video URL not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if no URL is provided
        }
    }
    private fun hideSystemUI() {
        // Enables immersive fullscreen mode
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    private fun showSystemUI() {
        // Restores the system UI to normal
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    override fun onDestroy() {
        super.onDestroy()
        // Restore the system UI when the activity is destroyed
        showSystemUI()
    }
}


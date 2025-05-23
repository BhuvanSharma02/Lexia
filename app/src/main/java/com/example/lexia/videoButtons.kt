package com.example.lexia

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class videoButtons : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_buttons)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val journeys = listOf(
            Journey("Basecamp", "Videos 1–5", 1, 5, R.drawable.bg_basecamp),
            Journey("Journey 1", "Videos 6–34", 6, 34, R.drawable.bg_journey1),
            Journey("Journey 2", "Videos 35–68", 35, 68, R.drawable.bg_journey2),
            Journey("Journey 3", "Videos 69–93", 69, 93, R.drawable.bg_journey3)
        )

        recyclerView.adapter = JourneyAdapter(journeys)
    }
}

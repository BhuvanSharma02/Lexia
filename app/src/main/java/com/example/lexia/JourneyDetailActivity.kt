package com.example.lexia

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class JourneyDetailActivity : AppCompatActivity() {

    private val videoIds = listOf(
        "454875655", "454884856", "454885391",
        // Add remaining fixed video IDs (up to 93) in correct order
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_detail)

        val startIndex = intent.getIntExtra("START", 1)
        val endIndex = intent.getIntExtra("END", 1)
        val backgroundResId = intent.getIntExtra("BACKGROUND_RES_ID", R.drawable.bg_basecamp)

        val scrollView = findViewById<ScrollView>(R.id.rootScrollView)
        val backgroundDrawable: Drawable? = ContextCompat.getDrawable(this, backgroundResId)
        scrollView.background = backgroundDrawable
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)


        val heightInDp = 72
        val scale = resources.displayMetrics.density
        val heightInPx = (heightInDp * scale + 0.5f).toInt()

        for (i in startIndex..endIndex) {
            val button = Button(this).apply {
                text = i.toString()
                textSize = 16f
                setBackgroundResource(R.drawable.rounded_button)
                setOnClickListener {
                    val videoId = videoIds.getOrNull(i - 1) // 0-based index
                    if (videoId != null) {
                        val intent = Intent(this@JourneyDetailActivity, VideoPlayerActivity::class.java)
                        intent.putExtra("videoId", videoId)
                        startActivity(intent)
                    }
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = heightInPx
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(16, 16, 16, 16)
            }
            gridLayout.addView(button, params)
        }

    }
}

package com.example.lexia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class Home : AppCompatActivity() {

    private lateinit var greetingText: TextView
    private lateinit var streakCount: TextView
    private lateinit var badgeType: TextView
    private lateinit var badgeDesc: TextView
    private lateinit var badgeIcon: ImageView
    private lateinit var fireIcon: ImageView

    private val PREFS_NAME = "StreakPrefs"
    private val KEY_STREAK = "streak"
    private val KEY_LAST_OPENED = "lastOpened"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        greetingText = findViewById(R.id.greetingText)
        streakCount = findViewById(R.id.streakCount)
        badgeType = findViewById(R.id.badgeType)
        badgeDesc = findViewById(R.id.badgeDesc)
        badgeIcon = findViewById(R.id.badgeIcon)
        fireIcon = findViewById(R.id.fireIcon)

        updateGreeting()
        updateStreak()
        val intent = Intent(this,videoButtons::class.java)
        val videoBtn = findViewById<ImageButton>(R.id.videos)
        videoBtn.setOnClickListener{
            startActivity(intent)
        }

        val intent1 = Intent(this, Categories::class.java)
        val gameBtn = findViewById<ImageButton>(R.id.games)
        gameBtn.setOnClickListener{
            startActivity(intent1)
        }

        val intent2 = Intent(this, DifficultyActivity::class.java)
        val proBtn = findViewById<Button>(R.id.tts)
        proBtn.setOnClickListener(){
            startActivity(intent2)
        }

        val intent3 = Intent(this, HomophonesLevelSelection::class.java)
        val quizBtn = findViewById<ImageButton>(R.id.quiz)
        quizBtn.setOnClickListener(){
            startActivity(intent3)
        }

        val intent4 = Intent(this, Level_Syllable::class.java)
        val sylbtn = findViewById<ImageButton>(R.id.syllable)
        sylbtn.setOnClickListener(){
            startActivity(intent4)
        }
    }



    private fun updateGreeting() {
        val username = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("username", "Friend")

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..22 -> "Good Evening"
            else -> "Good Night"
        }

        greetingText.text = "$greeting, $username! 👋"
    }

    private fun setTextColor(hexColor: String) {
        val color = android.graphics.Color.parseColor(hexColor)
        streakCount.setTextColor(color)
        badgeType.setTextColor(color)
        badgeDesc.setTextColor(color)
    }

    private fun updateStreak() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastOpened = prefs.getString(KEY_LAST_OPENED, null)
        val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        var streak = prefs.getInt(KEY_STREAK, 0)

        if (lastOpened == null) {
            // First time, show 0
            streak = 0
        } else if (lastOpened != currentDate) {
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val lastDate = sdf.parse(lastOpened)
            val diff = Date().time - lastDate!!.time
            val days = diff / (1000 * 60 * 60 * 24)

            streak = when {
                days == 1L -> streak + 1
                days > 1L -> 0 // streak reset
                else -> streak
            }
        }

        // Save updated streak and lastOpened
        prefs.edit()
            .putInt(KEY_STREAK, streak)
            .putString(KEY_LAST_OPENED, currentDate)
            .apply()

        // Animate streak
        streakCount.text = "$streak-Day Streak"
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        Handler(Looper.getMainLooper()).postDelayed({
        streakCount.startAnimation(anim)
        }, 700)


        val streakCard: View = findViewById(R.id.streakCard)

        when {
            streak >= 21 -> {
                badgeIcon.setImageResource(R.drawable.gold_badge)
                badgeType.text = "Gold Badge"
                badgeDesc.text = "You're a Gold Hero!"
                streakCard.setBackgroundResource(R.drawable.rounded_yellow)
                setTextColor("#003366")
            }
            streak >= 14 -> {
                badgeIcon.setImageResource(R.drawable.silver_badge)
                badgeType.text = "Silver Badge"
                badgeDesc.text = "You're a Silver Star!"
                streakCard.setBackgroundResource(R.drawable.rounded_silver)
                setTextColor("#333333")
            }
            streak >= 7 -> {
                badgeIcon.setImageResource(R.drawable.bronze_badge)
                badgeType.text = "Bronze Badge"
                badgeDesc.text = "You're on fire!"
                streakCard.setBackgroundResource(R.drawable.rounded_bronze)
                setTextColor("#FDF5E6")
            }
            else -> {
                badgeIcon.visibility = View.GONE
                badgeType.text = ""
                badgeDesc.text = ""
                streakCard.setBackgroundResource(R.drawable.rounded_neutral)
                setTextColor("#5C4033")
            }
        }


        fireIcon.setImageResource(
            if (streak == 0) R.drawable.streak_0 else R.drawable.streak_active
        )
    }
}

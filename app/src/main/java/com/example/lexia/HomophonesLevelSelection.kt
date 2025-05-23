package com.example.lexia

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomophonesLevelSelection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homophones_level_selection)

        val level1Btn = findViewById<Button>(R.id.btnLevel1)
        val level2Btn = findViewById<Button>(R.id.btnLevel2)
        val level3Btn = findViewById<Button>(R.id.btnLevel3)

        level1Btn.setOnClickListener {
            openHomophoneGame("1")
        }

        level2Btn.setOnClickListener {
            openHomophoneGame("2")
        }

        level3Btn.setOnClickListener {
            openHomophoneGame("3")
        }
    }

    private fun openHomophoneGame(level: String) {
        val intent = Intent(this, HomophoneActivity::class.java)
        intent.putExtra("LEVEL", level)
        startActivity(intent)
    }
}

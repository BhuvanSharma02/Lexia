package com.example.lexia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity

class GameButtonGridActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button_grid)

        val buttonGrid = findViewById<GridLayout>(R.id.buttonGrid)
        val inflater = LayoutInflater.from(this)

        // Get the selected category from the intent
        val selectedCategory = intent.getStringExtra("CATEGORY")

        // Load words and images based on the selected category
        val words = when (selectedCategory) {
            "Fruits" -> listOf(
                "APPLE" to R.drawable.apple,
                "BANANA" to R.drawable.banana,
                "ORANGE" to R.drawable.orange,
                "GRAPES" to R.drawable.grapes,
                "WATERMELON" to R.drawable.watermelon
                // Add more fruits
            )
            "Vegetables" -> listOf(
                "CARROT" to R.drawable.carrot,
                "TOMATO" to R.drawable.tomato
//                "POTATO" to R.drawable.potato,
//                "CUCUMBER" to R.drawable.cucumber,
//                "ONION" to R.drawable.onion
                // Add more vegetables
            )
            // Add more categories as needed
            else -> emptyList()
        }

        val heightInDp = 72
        val scale = resources.displayMetrics.density
        val heightInPx = (heightInDp * scale + 0.5f).toInt()

        // Generate buttons for the words in the selected category
        for ((index, wordAndImage) in words.withIndex()) {
            val (word, imageResId) = wordAndImage

            val button = inflater.inflate(R.layout.item_button, buttonGrid, false) as Button
            button.text = "${index + 1}"

            button.setOnClickListener {
                val intent = Intent(this@GameButtonGridActivity, GameActivity::class.java)
                intent.putExtra("WORD", word)
                intent.putExtra("IMAGE_RES_ID", imageResId)
                startActivity(intent)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = heightInPx
                columnSpec = GridLayout.spec(index % 3, 1f)
                rowSpec = GridLayout.spec(index / 3)
                setMargins(8, 8, 8, 8)
            }
            button.layoutParams = params

            buttonGrid.addView(button)
        }
    }
}
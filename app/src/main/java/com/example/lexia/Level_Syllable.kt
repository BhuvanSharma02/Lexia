package com.example.lexia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lexia.databinding.ActivityLevelSyllableBinding

class Level_Syllable : AppCompatActivity() {

    private lateinit var binding: ActivityLevelSyllableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSyllableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val levelWords = mapOf(
            1 to Triple("Happiness", listOf("hap", "pi", "ness"), "A feeling of joy or contentment"),
            2 to Triple("Interesting", listOf("in", "ter", "est", "ing"), "Something that catches attention"),
            3 to Triple("Beautiful", listOf("beau", "ti", "ful"), "Pleasing to the senses or mind"),
            4 to Triple("Adventure", listOf("ad", "ven", "ture"), "An exciting experience")
        )

        binding.level1.setOnClickListener {
            startSyllableActivity(levelWords[1]!!)
        }
        binding.level2.setOnClickListener {
            startSyllableActivity(levelWords[2]!!)
        }
        binding.level3.setOnClickListener {
            startSyllableActivity(levelWords[3]!!)
        }
        binding.level4.setOnClickListener {
            startSyllableActivity(levelWords[4]!!)
        }
    }

    private fun startSyllableActivity(data: Triple<String, List<String>, String>) {
        val intent = Intent(this, SyllableActivity::class.java).apply {
            putExtra("word", data.first)
            putStringArrayListExtra("syllables", ArrayList(data.second))
            putExtra("meaning", data.third)
        }
        startActivity(intent)
    }
}

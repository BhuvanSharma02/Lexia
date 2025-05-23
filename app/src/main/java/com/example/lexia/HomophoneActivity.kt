package com.example.lexia

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.util.*

class HomophoneActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var sentenceText: TextView
    private lateinit var playSentenceBtn: ImageButton
    private lateinit var optionBuy: TextView
    private lateinit var optionBy: TextView
    private lateinit var optionBye: TextView
    private lateinit var playBuy: ImageButton
    private lateinit var playBy: ImageButton
    private lateinit var playBye: ImageButton
    private lateinit var meaningBox: TextView
    private lateinit var optionBuyContainer: LinearLayout
    private lateinit var optionByContainer: LinearLayout
    private lateinit var optionByeContainer: LinearLayout

    private val correctAnswer = "buy"
    private var isAnswerSelected = false

    // Dictionary for word meanings
    private val wordMeanings = mapOf(
        "buy" to "To obtain something by paying money for it",
        "by" to "Identifying the author or means of something",
        "bye" to "A farewell or good wishes when parting"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homophone)

        tts = TextToSpeech(this, this)

        // Initialize all views
        sentenceText = findViewById(R.id.sentenceText)
        playSentenceBtn = findViewById(R.id.playSentenceBtn)
        optionBuy = findViewById(R.id.optionBuy)
        optionBy = findViewById(R.id.optionBy)
        optionBye = findViewById(R.id.optionBye)
        playBuy = findViewById(R.id.playBuy)
        playBy = findViewById(R.id.playBy)
        playBye = findViewById(R.id.playBye)
        meaningBox = findViewById(R.id.meaningBox)
        optionBuyContainer = findViewById(R.id.optionBuyContainer)
        optionByContainer = findViewById(R.id.optionByContainer)
        optionByeContainer = findViewById(R.id.optionByeContainer)

        // Play sentence on button click
        playSentenceBtn.setOnClickListener {
            speak("I want to buy a new book.")
        }

        // Play word pronunciations
        playBuy.setOnClickListener { speak("buy") }
        playBy.setOnClickListener { speak("by") }
        playBye.setOnClickListener { speak("bye") }

        // Answer selection logic
        val answerClickListener = fun(answer: String) {
            if (isAnswerSelected) return

            isAnswerSelected = true
            showMeaning(answer)

            // Reset all to default ripple background (no tint)
            clearAllTints()

            if (answer == correctAnswer) {
                tintContainer(optionBuyContainer, R.color.correct_green)
                Toast.makeText(this, "Well done!", Toast.LENGTH_SHORT).show()
            } else {
                // Tint selected wrong answer red
                when (answer) {
                    "buy" -> tintContainer(optionBuyContainer, R.color.incorrect_red)
                    "by" -> tintContainer(optionByContainer, R.color.incorrect_red)
                    "bye" -> tintContainer(optionByeContainer, R.color.incorrect_red)
                }
                // Tint correct answer green
                tintContainer(optionBuyContainer, R.color.correct_green)
                Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show()
            }
        }

        // Attach listeners to all options
        optionBuy.setOnClickListener { answerClickListener("buy") }
        optionBy.setOnClickListener { answerClickListener("by") }
        optionBye.setOnClickListener { answerClickListener("bye") }

        optionBuyContainer.setOnClickListener { answerClickListener("buy") }
        optionByContainer.setOnClickListener { answerClickListener("by") }
        optionByeContainer.setOnClickListener { answerClickListener("bye") }
    }

    private fun showMeaning(word: String) {
        meaningBox.text = wordMeanings[word] ?: "Meaning not available"
    }

    private fun tintContainer(container: LinearLayout, colorResId: Int) {
        ViewCompat.setBackgroundTintList(
            container,
            ContextCompat.getColorStateList(this, colorResId)
        )
    }

    private fun clearAllTints() {
        ViewCompat.setBackgroundTintList(optionBuyContainer, null)
        ViewCompat.setBackgroundTintList(optionByContainer, null)
        ViewCompat.setBackgroundTintList(optionByeContainer, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}

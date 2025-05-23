package com.example.lexia

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lexia.databinding.ActivitySyllablesBinding
import java.util.*

class SyllableActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivitySyllablesBinding
    private lateinit var tts: TextToSpeech
    private val SPEECH_REQUEST = 101
    private var correctWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyllablesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        correctWord = intent.getStringExtra("word") ?: ""
        val syllables = intent.getStringArrayListExtra("syllables") ?: arrayListOf()
        val meaning = intent.getStringExtra("meaning") ?: ""

        binding.wordText.text = correctWord

        syllables.forEachIndexed { index, syllable ->
            val button = Button(this).apply {
                text = syllable
                setBackgroundColor(getSyllableColor(index))
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                setPadding(20, 10, 20, 10)
                setOnClickListener {
                    tts.speak(syllable, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            binding.syllableLayout.addView(button)
        }

        binding.meaningText.text = meaning

        binding.micButton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the word")
            }
            startActivityForResult(intent, SPEECH_REQUEST)
        }
    }

    private fun getSyllableColor(index: Int): Int {
        val colors = listOf(
            R.color.syllable1, R.color.syllable2, R.color.syllable3, R.color.syllable4
        )
        return ContextCompat.getColor(this, colors[index % colors.size])
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenWord = results?.firstOrNull()?.lowercase(Locale.ROOT) ?: ""
            binding.userSpokenText.text = spokenWord
            if (spokenWord == correctWord.lowercase(Locale.ROOT)) {
                binding.feedbackText.text = "Correct!"
                binding.feedbackText.setTextColor(ContextCompat.getColor(this, R.color.correctGreen))
                playSuccessSound()
            } else {
                binding.feedbackText.text = "Try again"
                binding.feedbackText.setTextColor(ContextCompat.getColor(this, R.color.errorRed))
            }
        }
    }

    private fun playSuccessSound() {
        val mp = MediaPlayer.create(this, R.raw.correct_sound)
        mp.setOnCompletionListener { it.release() }
        mp.start()
    }
}

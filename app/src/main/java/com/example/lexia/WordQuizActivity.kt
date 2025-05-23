package com.example.lexia

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lexia.databinding.ActivityWordQuizBinding


class WordQuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordQuizBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var currentWord: String
    private val REQUEST_RECORD_AUDIO = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val level = intent.getParcelableExtra<Level>("level")!!
        val color = intent.getIntExtra("difficultyColor", android.R.color.holo_green_light)
        currentWord = level.words.random()

        // Initialize UI
        with(binding) {
            levelIndicator.text = "Level ${level.number}"
            levelIndicator.setTextColor(ContextCompat.getColor(this@WordQuizActivity, color))
            tvWord.text = currentWord

            btnBreakDown.setOnClickListener {
                val syllables = breakIntoSyllables(currentWord)
                Intent(this@WordQuizActivity, SyllableActivity::class.java).apply {
                    putExtra("word", currentWord)
                    putExtra("syllables", ArrayList(syllables)) // Corrected this line
                    startActivity(this)
                }
            }

            // Initialize Speech Recognizer
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@WordQuizActivity).apply {
                setRecognitionListener(createRecognitionListener())
            }

            btnMic.setOnClickListener {
                checkAudioPermission()
            }
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO
            )
        } else {
            startListening()
        }
    }

    private fun startListening() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: $currentWord")
            }
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            val errorMsg = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission missing"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Toast.makeText(this@WordQuizActivity, errorMsg, Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle) {
            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { spokenText ->
                if (spokenText.equals(currentWord, ignoreCase = true)) {
                    Toast.makeText(this@WordQuizActivity, "Correct! 🎉", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@WordQuizActivity, "Try again!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        speechRecognizer.destroy()
        super.onDestroy()
    }

    private val syllableDictionary = mapOf(
        "cat" to listOf("cat"),
        "dog" to listOf("dog"),
        "sun" to listOf("sun"),
        "elephant" to listOf("el", "e", "phant"),
        "computer" to listOf("com", "pu", "ter"),
        "extravaganza" to listOf("ex", "tra", "va", "gan", "za"),
        "interesting" to listOf("in", "ter", "est", "ing"),
        "butterfly" to listOf("but", "ter", "fly"),
        "umbrella" to listOf("um", "brel", "la"),
        "hospital" to listOf("hos", "pi", "tal")
    )

    private fun breakIntoSyllables(word: String): List<String> {
        return syllableDictionary[word.lowercase()] ?: run {
            // Fallback algorithm for words not in dictionary
            val vowels = setOf('a', 'e', 'i', 'o', 'u')
            val syllables = mutableListOf<String>()
            var currentSyllable = ""

            word.lowercase().forEach { char ->
                currentSyllable += char
                if (vowels.contains(char) && currentSyllable.length > 1) {
                    syllables.add(currentSyllable)
                    currentSyllable = ""
                }
            }

            if (currentSyllable.isNotEmpty()) {
                syllables.add(currentSyllable)
            }

            if (syllables.isEmpty()) listOf(word) else syllables
        }
    }
}
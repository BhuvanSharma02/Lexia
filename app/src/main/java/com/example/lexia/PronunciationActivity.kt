package com.example.lexia

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class PronunciationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val TAG = "PronunciationActivity"
    private val REQUEST_AUDIO_PERMISSION = 200

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textToSpeech: TextToSpeech
    private var isListening = false
    private lateinit var currentWord: String

    // UI Components
    private lateinit var micButton: ImageView
    private lateinit var listeningStatus: TextView

    // Practice items
    private val practiceItems = listOf(
        PracticeItem("apple", R.drawable.apple),
        PracticeItem("banana", R.drawable.banana)
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation)

        // Initialize UI
        micButton = findViewById(R.id.micButton)
        listeningStatus = findViewById(R.id.listeningStatus)

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        // Check permissions
        checkAudioPermission()

        // Setup speech recognizer
        setupSpeechRecognizer()

        // Set click listeners
        micButton.setOnClickListener { toggleSpeechRecognition() }
        findViewById<TextView>(R.id.hearCorrectButton).setOnClickListener { speakCorrectPronunciation() }
        findViewById<TextView>(R.id.nextButton).setOnClickListener { showNextItem() }

        // Show first word
        showNextItem()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    runOnUiThread {
                        listeningStatus.visibility = View.VISIBLE
                        listeningStatus.text = "Listening..."
                    }
                }

                override fun onBeginningOfSpeech() {}

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    runOnUiThread {
                        if (isListening) {
                            toggleSpeechRecognition()
                        }
                    }
                }

                override fun onError(error: Int) {
                    runOnUiThread {
                        // Don't show error if we intentionally stopped
                        if (error != SpeechRecognizer.ERROR_CLIENT || !isListening) {
                            val errorMsg = when (error) {
                                SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech input timeout"
                                else -> "Error: ${getErrorText(error)}"
                            }
                            if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                                Toast.makeText(this@PronunciationActivity, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                        if (isListening) {
                            toggleSpeechRecognition()
                        }
                    }
                }

                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        evaluatePronunciation(matches[0])
                    }
                    runOnUiThread {
                        toggleSpeechRecognition()
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun toggleSpeechRecognition() {
        if (isListening) {
            // Stop listening and clean up
            runCatching {
                speechRecognizer.stopListening()
                speechRecognizer.destroy()
            }.onFailure {
                Log.e(TAG, "Error stopping recognizer", it)
            }
            isListening = false
            updateMicState()
        } else {
            // Set up fresh recognizer instance
            setupSpeechRecognizer()
            runCatching {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
                speechRecognizer.startListening(intent)
                isListening = true
                updateMicState()
            }.onFailure {
                Log.e(TAG, "Error starting recognition", it)
                speechRecognizer.destroy()
                isListening = false
                updateMicState()
            }
        }
    }

    private fun updateMicState() {
        runOnUiThread {
            micButton.setImageResource(
                if (isListening) R.drawable.ic_mic_on else R.drawable.ic_mic_off
            )
            listeningStatus.visibility = if (isListening) View.VISIBLE else View.GONE
        }
    }

    private fun showNextItem() {
        if (currentIndex < practiceItems.size) {
            val item = practiceItems[currentIndex]
            currentWord = item.word
            findViewById<TextView>(R.id.wordText).text = item.word
            findViewById<ImageView>(R.id.imageView).setImageResource(item.imageRes)
            findViewById<TextView>(R.id.resultText).text = ""
            currentIndex++
        } else {
            Toast.makeText(this, "Practice completed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun evaluatePronunciation(spokenText: String) {
        val isCorrect = spokenText.equals(currentWord, ignoreCase = true)
        val resultTextView = findViewById<TextView>(R.id.resultText)

        if (isCorrect) {
            resultTextView.text = "Correct! Well done!"
            resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            resultTextView.text = "Try again. You said: $spokenText"
            resultTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }
    }

    private fun speakCorrectPronunciation() {
        textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.getDefault()
        } else {
            Toast.makeText(this, "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQUEST_AUDIO_PERMISSION
            )
        }
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Permission needed for speech recognition", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        textToSpeech.shutdown()
    }

    data class PracticeItem(val word: String, val imageRes: Int)
}
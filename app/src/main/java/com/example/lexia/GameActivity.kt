package com.example.lexia

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.airbnb.lottie.LottieAnimationView
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import java.util.Locale

class GameActivity : AppCompatActivity() {

    private lateinit var lettersContainer: GridLayout
    private lateinit var wordText: TextView
    private lateinit var imageView: ImageView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var correctWord: String
    private lateinit var jumbledWord: String
    private var currentPosition = 0 // Track the current position in the word
    private lateinit var selectedLetters: MutableList<Char?> // Track selected letters
    private val letterButtons = mutableListOf<Button>() // Track all letter buttons
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var mediaPlayer: MediaPlayer // MediaPlayer for sound playback
    private lateinit var clickSound: MediaPlayer // MediaPlayer for sound playback
    private lateinit var disappearAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize views
        lettersContainer = findViewById(R.id.lettersContainer)
        wordText = findViewById(R.id.dottedText)
        imageView = findViewById(R.id.imageView)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)

        //Button Animation
        disappearAnimation = AnimationUtils.loadAnimation(this, R.anim.button_disappear)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.correct_sound) // Load the sound file

        // Get the word and image resource ID from the intent
        correctWord = intent.getStringExtra("WORD") ?: "APPLE"
        val imageResId = intent.getIntExtra("IMAGE_RES_ID", R.drawable.apple)

        // Initialize selectedLetters after correctWord is set
        selectedLetters = MutableList(correctWord.length) { null }

        // Set the image
        imageView.setImageResource(imageResId)

        // Generate the jumbled word
        jumbledWord = correctWord.toList().shuffled().joinToString("")

        // Set the initial word text in dotted font
        wordText.text = correctWord

        // Set up the jumbled letters
        setupJumbledLetters()

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set the language for TTS
                val result = textToSpeech.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Text-to-speech language not supported", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        // Speak the word when the image is clicked
        imageView.setOnClickListener {
            speakWord(correctWord)
        }
    }

    private fun setupJumbledLetters() {
        val gridLayout = findViewById<GridLayout>(R.id.lettersContainer)
        clickSound = MediaPlayer.create(this, R.raw.pop)
        gridLayout.removeAllViews() // Clear existing views

        val columnCount = 4
        val rowCount = (jumbledWord.length + columnCount - 1) / columnCount

        gridLayout.columnCount = 4
        gridLayout.rowCount = rowCount

        for ((index, letter) in jumbledWord.withIndex()) {
            val letterButton = Button(this, null, 0, R.style.JumbledLetterButton).apply {
                text = letter.toString()
                contentDescription = "Letter $letter" // Accessibility
                textSize = 18f

                isSoundEffectsEnabled = false

                setOnClickListener {
                    onLetterClick(letter, this)
                }
            }

            // Add the button to the GridLayout
            val buttonSize = resources.getDimensionPixelSize(R.dimen.jumbled_letter_button_size)
            val params = GridLayout.LayoutParams().apply {
                width = buttonSize
                height = buttonSize
                columnSpec = GridLayout.spec(index % columnCount, 1f) // Adjust column count
                rowSpec = GridLayout.spec(index / columnCount)
                setMargins(8, 8, 8, 8)
            }
            letterButton.layoutParams = params

            gridLayout.addView(letterButton)
            letterButtons.add(letterButton) // Add button to the list
        }
    }

    private fun onLetterClick(letter: Char, button: Button) {
        // Check if the clicked letter matches the expected letter
        if (currentPosition < correctWord.length && letter == correctWord[currentPosition]) {
            // Replace the letter in the word text with Tilt Warp font
            updateWordText(currentPosition, letter)
            clickSound.start()
            button.startAnimation(disappearAnimation)
            // Remove the correct letter from the jumbled letters
            button.postDelayed({
                lettersContainer.removeView(button)
                letterButtons.remove(button)
            }, disappearAnimation.duration)

            // Move to the next position
            currentPosition++

            // Check if the word is complete
            if (currentPosition == correctWord.length) {
                showCorrectAnimation()
                playCorrectSound() // Play the sound
            }
        } else {
            // Vibrate for incorrect letter
            vibrate()
        }
    }

    private fun updateWordText(position: Int, letter: Char) {
        val spannableString = SpannableStringBuilder(wordText.text)

        // Replace the character at the specified position
        spannableString.replace(position, position + 1, letter.toString())

        // Apply the Tilt Warp font to the correct letter
        val tiltWarpTypeface = ResourcesCompat.getFont(this, R.font.tilt_warp)
        spannableString.setSpan(
            CustomTypefaceSpan(tiltWarpTypeface),
            position,
            position + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        wordText.text = spannableString
    }

    private fun showCorrectAnimation() {
        try {
            Log.d("GameActivity", "showCorrectAnimation called")

            // Make the LottieAnimationView visible
            lottieAnimationView.visibility = View.VISIBLE

            // Set the animation file
            lottieAnimationView.setAnimation("correct.json")
            Log.d("GameActivity", "Animation file set")

            // Play the animation
            lottieAnimationView.playAnimation()
            Log.d("GameActivity", "Animation started")

            // Show a toast message
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("GameActivity", "Error playing animation: ${e.message}")
        }
    }

    private fun playCorrectSound() {
        try {
            if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
                mediaPlayer.start() // Play the sound
            }
        } catch (e: Exception) {
            Log.e("GameActivity", "Error playing sound: ${e.message}")
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    private fun speakWord(word: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroy() {
        // Release MediaPlayer resources
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        if (::clickSound.isInitialized) {
            clickSound.release() // ✅ Free up resources
        }

        // Shutdown TextToSpeech when the activity is destroyed
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }


        super.onDestroy()
    }
}
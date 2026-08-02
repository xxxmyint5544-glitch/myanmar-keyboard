package com.myanmar.keyboard

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class VoiceInputActivity : Activity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var micEmoji: TextView
    private lateinit var statusText: TextView
    private lateinit var langText: TextView
    private var pulseAnimatorX: ObjectAnimator? = null
    private var pulseAnimatorY: ObjectAnimator? = null

    private val permissionRequestCode = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_input)

        micEmoji = findViewById(R.id.micEmoji)
        statusText = findViewById(R.id.statusText)
        langText = findViewById(R.id.langText)
        val btnStop = findViewById<Button>(R.id.btnStop)

        langText.text = languageLabel(MyanmarKeyboardService.voiceLanguage)

        btnStop.setOnClickListener { finishSession() }
        findViewById<android.view.View>(R.id.rootLayout).setOnClickListener { finishSession() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), permissionRequestCode
            )
        } else {
            startListening()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                Toast.makeText(this, "Microphone ခွင့်ပြုချက် လိုအပ်ပါသည်", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "ဤစက်ပေါ်တွင် အသံဖြင့်စာရိုက်ခြင်း မရနိုင်ပါ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                statusText.text = "နားထောင်နေသည်..."
                startPulse()
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                statusText.text = "လုပ်ဆောင်နေသည်..."
            }

            override fun onError(error: Int) {
                stopPulse()
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> restartListening()
                    else -> {
                        Toast.makeText(
                            this@VoiceInputActivity,
                            "အသံမှတ်သားရာတွင် အမှားရှိပါသည်",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrEmpty()) {
                    MyanmarKeyboardService.instance?.commitVoiceText("$text ")
                }
                restartListening()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!partial.isNullOrEmpty()) {
                    statusText.text = partial
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        launchRecognition()
    }

    private fun launchRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, MyanmarKeyboardService.voiceLanguage)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun restartListening() {
        if (!isFinishing) {
            statusText.text = "နားထောင်နေသည်..."
            launchRecognition()
        }
    }

    private fun startPulse() {
        pulseAnimatorX?.cancel()
        pulseAnimatorY?.cancel()
        pulseAnimatorX = ObjectAnimator.ofFloat(micEmoji, "scaleX", 1f, 1.3f, 1f).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            start()
        }
        pulseAnimatorY = ObjectAnimator.ofFloat(micEmoji, "scaleY", 1f, 1.3f, 1f).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    private fun stopPulse() {
        pulseAnimatorX?.cancel()
        pulseAnimatorY?.cancel()
        micEmoji.scaleX = 1f
        micEmoji.scaleY = 1f
    }

    private fun finishSession() {
        stopPulse()
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.stopListening()
            speechRecognizer.destroy()
        }
        finish()
    }

    override fun onDestroy() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        super.onDestroy()
    }

    private fun languageLabel(code: String) = when (code) {
        "my-MM" -> "မြန်မာ"
        "en-US" -> "English"
        "th-TH" -> "ไทย"
        else -> code
    }
}

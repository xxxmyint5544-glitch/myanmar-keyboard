package com.myanmar.keyboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat

class MyanmarKeyboardService : InputMethodService() {

    companion object {
        var instance: MyanmarKeyboardService? = null
        var voiceLanguage: String = "my-MM"
    }

    private enum class Mode { LETTERS, NUMBERS, SYMBOLS }

    private lateinit var rowsContainer: LinearLayout
    private var mode = Mode.LETTERS
    private var isShift = false
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        stopVoiceListening()
        instance = null
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val keyboardView = LayoutInflater.from(this)
            .inflate(R.layout.keyboard_view, null) as LinearLayout
        rowsContainer = keyboardView.findViewById(R.id.rows_container)
        buildKeyboard()
        return keyboardView
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        stopVoiceListening()
    }

    private fun buildKeyboard() {
        rowsContainer.removeAllViews()

        if (isListening) {
            addRow(
                listOf(
                    KeyModel(
                        "🎤 နားထောင်နေသည်... (${languageLabel(voiceLanguage)}) — ရပ်ရန်နှိပ်ပါ",
                        action = KeyAction.VOICE,
                        flexWeight = 1f
                    )
                )
            )
            return
        }

        when (mode) {
            Mode.LETTERS -> {
                val row1 = if (isShift) MyanmarLayout.lettersRow2Shift else MyanmarLayout.lettersRow2
                val row2 = if (isShift) MyanmarLayout.lettersRow3Shift else MyanmarLayout.lettersRow3
                val row3Letters = if (isShift) MyanmarLayout.lettersRow4Shift else MyanmarLayout.lettersRow4

                addRow(MyanmarLayout.numbersRow1)
                addRow(row1)
                addRow(row2)
                addRow(
                    listOf(shiftKey()) + row3Letters + listOf(backspaceKey())
                )
                addRow(bottomRow(switchLabel = "123", switchAction = KeyAction.SWITCH_TO_NUMBERS))
            }
            Mode.NUMBERS -> {
                addRow(MyanmarLayout.numbersRow1)
                addRow(MyanmarLayout.numbersRow2)
                addRow(
                    listOf(KeyModel("#+=", action = KeyAction.SWITCH_TO_SYMBOLS, flexWeight = 1.5f)) +
                        MyanmarLayout.numbersRow3 + listOf(backspaceKey())
                )
                addRow(bottomRow(switchLabel = "ABC", switchAction = KeyAction.SWITCH_TO_LETTERS))
            }
            Mode.SYMBOLS -> {
                addRow(MyanmarLayout.symbolsRow1)
                addRow(MyanmarLayout.symbolsRow2)
                addRow(
                    listOf(KeyModel("123", action = KeyAction.SWITCH_TO_NUMBERS, flexWeight = 1.5f)) +
                        MyanmarLayout.symbolsRow3 + listOf(backspaceKey())
                )
                addRow(bottomRow(switchLabel = "ABC", switchAction = KeyAction.SWITCH_TO_LETTERS))
            }
        }
    }

    private fun languageLabel(code: String) = when (code) {
        "my-MM" -> "မြန်မာ"
        "en-US" -> "English"
        "th-TH" -> "ไทย"
        else -> code
    }

    private fun shiftKey() = KeyModel(label = "⇧", action = KeyAction.SHIFT, flexWeight = 1.5f)

    private fun backspaceKey() = KeyModel("⌫", action = KeyAction.BACKSPACE, flexWeight = 1.5f)

    private fun micKey() = KeyModel("🎤", action = KeyAction.VOICE, flexWeight = 1.2f)

    private fun bottomRow(switchLabel: String, switchAction: KeyAction) = listOf(
        KeyModel(switchLabel, action = switchAction, flexWeight = 1.3f),
        micKey(),
        KeyModel(",", flexWeight = 0.9f),
        KeyModel("space", output = " ", action = KeyAction.SPACE, flexWeight = 3.2f),
        KeyModel(".", flexWeight = 0.9f),
        KeyModel("Enter", action = KeyAction.ENTER, flexWeight = 1.3f)
    )

    private fun addRow(keys: List<KeyModel>) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        keys.forEach { key -> row.addView(createKeyButton(key)) }
        rowsContainer.addView(row)
    }

    private fun createKeyButton(key: KeyModel): Button {
        val isSpecial = key.action != KeyAction.NONE
        return Button(this).apply {
            text = key.label
            isAllCaps = false
            textSize = if (isListening) 14f else 18f
            setTextColor(ContextCompat.getColor(context, R.color.key_text))
            background = ContextCompat.getDrawable(
                context,
                if (isSpecial) R.drawable.key_bg_special_selector else R.drawable.key_bg_selector
            )
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                key.flexWeight
            ).apply {
                setMargins(2, 2, 2, 2)
                height = (56 * resources.displayMetrics.density).toInt()
            }
            setOnClickListener { onKey(key) }
            if (key.action == KeyAction.VOICE && !isListening) {
                setOnLongClickListener {
                    cycleVoiceLanguage()
                    true
                }
            }
        }
    }

    private fun cycleVoiceLanguage() {
        voiceLanguage = when (voiceLanguage) {
            "my-MM" -> "en-US"
            "en-US" -> "th-TH"
            else -> "my-MM"
        }
        Toast.makeText(this, languageLabel(voiceLanguage), Toast.LENGTH_SHORT).show()
    }

    private fun onKey(key: KeyModel) {
        val ic = currentInputConnection

        when (key.action) {
            KeyAction.BACKSPACE -> ic?.deleteSurroundingText(1, 0)
            KeyAction.SHIFT -> {
                isShift = !isShift
                buildKeyboard()
            }
            KeyAction.SPACE -> ic?.commitText(" ", 1)
            KeyAction.ENTER -> ic?.commitText("\n", 1)
            KeyAction.SWITCH_TO_NUMBERS -> {
                mode = Mode.NUMBERS
                buildKeyboard()
            }
            KeyAction.SWITCH_TO_SYMBOLS -> {
                mode = Mode.SYMBOLS
                buildKeyboard()
            }
            KeyAction.SWITCH_TO_LETTERS -> {
                mode = Mode.LETTERS
                buildKeyboard()
            }
            KeyAction.VOICE -> {
                if (isListening) stopVoiceListening() else toggleVoiceListening()
            }
            KeyAction.NONE -> {
                commitLetter(ic, key.output)
                if (isShift) {
                    isShift = false
                    buildKeyboard()
                }
            }
        }
    }

    private fun commitLetter(ic: android.view.inputmethod.InputConnection?, output: String) {
        if (ic == null) return

        if (output == "ေ") {
            val before = ic.getTextBeforeCursor(1, 0)
            val prevChar = before?.firstOrNull()
            if (prevChar != null && prevChar.code in 0x1000..0x1021) {
                ic.deleteSurroundingText(1, 0)
                ic.commitText("ေ$prevChar", 1)
                return
            }
        }
        ic.commitText(output, 1)
    }

    private fun toggleVoiceListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "App ကိုဖွင့်ပြီး \"Enable Voice Typing\" ကိုနှိပ်ပါ",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }
        startVoiceListening()
    }

    private fun startVoiceListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "ဤစက်ပေါ်တွင် အသံဖြင့်စာရိုက်ခြင်း မရနိုင်ပါ", Toast.LENGTH_SHORT).show()
            return
        }

        isListening = true
        buildKeyboard()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH,
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                            if (isListening) launchRecognition()
                        }
                        else -> stopVoiceListening()
                    }
                }

                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                    if (!text.isNullOrEmpty()) {
                        currentInputConnection?.commitText("$text ", 1)
                    }
                    if (isListening) launchRecognition()
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        launchRecognition()
    }

    private fun launchRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, voiceLanguage)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun stopVoiceListening() {
        isListening = false
        speechRecognizer?.destroy()
        speechRecognizer = null
        buildKeyboard()
    }
}

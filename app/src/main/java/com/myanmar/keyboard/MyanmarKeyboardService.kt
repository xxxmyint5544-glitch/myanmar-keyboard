package com.myanmar.keyboard

import android.content.Intent
import android.inputmethodservice.InputMethodService
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

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
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

    fun commitVoiceText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    private fun buildKeyboard() {
        rowsContainer.removeAllViews()

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

    private fun shiftKey() = KeyModel(
        label = if (isShift) "⇧" else "⇧",
        action = KeyAction.SHIFT,
        flexWeight = 1.5f
    )

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
            textSize = 18f
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
            if (key.action == KeyAction.VOICE) {
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
        val label = when (voiceLanguage) {
            "my-MM" -> "မြန်မာ အသံဖမ်းစနစ်"
            "en-US" -> "English voice"
            else -> "ภาษาไทย"
        }
        Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
    }

    private fun onKey(key: KeyModel) {
        val ic = currentInputConnection ?: return

        when (key.action) {
            KeyAction.BACKSPACE -> ic.deleteSurroundingText(1, 0)
            KeyAction.SHIFT -> {
                isShift = !isShift
                buildKeyboard()
            }
            KeyAction.SPACE -> ic.commitText(" ", 1)
            KeyAction.ENTER -> ic.commitText("\n", 1)
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
                val intent = Intent(this, VoiceInputActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            KeyAction.NONE -> {
                ic.commitText(key.output, 1)
                if (isShift) {
                    isShift = false
                    buildKeyboard()
                }
            }
        }
    }
}

package com.myanmar.keyboard

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

class MyanmarKeyboardService : InputMethodService() {

    companion object {
        var instance: MyanmarKeyboardService? = null
            private set
    }

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
        // Gboard ပုံစံတူ အမိုက်စား အမှောင်ရောင် Layout ကို ချိတ်ဆက်ခြင်း
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        return keyboardView
    }

    fun commitText(text: String) {
        val currentConnection = currentInputConnection
        currentConnection?.commitText(text, 1)
    }

    fun deleteSurroundingText() {
        val currentConnection = currentInputConnection
        currentConnection?.deleteSurroundingText(1, 0)
    }
}

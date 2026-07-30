package com.myanmar.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View

class MyanmarKeyboardService : InputMethodService() {

    companion object {
        var instance: MyanmarKeyboardService? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        // အခြေခံ ကီးဘုတ် Layout ကို ချိတ်ဆက်ခြင်း
        return layoutInflater.inflate(R.layout.keyboard_view, null)
    }

    fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    fun deleteSurroundingText() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }
}

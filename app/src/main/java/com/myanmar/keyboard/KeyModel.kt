package com.myanmar.keyboard

data class KeyModel(
    val label: String,
    val output: String = label,
    val action: KeyAction = KeyAction.NONE,
    val flexWeight: Float = 1f
)

enum class KeyAction {
    NONE,
    BACKSPACE,
    SHIFT,
    SPACE,
    ENTER,
    SWITCH_TO_NUMBERS,
    SWITCH_TO_SYMBOLS,
    SWITCH_TO_LETTERS,
    VOICE
}

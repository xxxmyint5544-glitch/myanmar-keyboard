package com.myanmar.keyboard

/**
 * Represents a single key on the keyboard.
 *
 * @param label What is drawn on the key (may differ from [output], e.g. "⌫").
 * @param output The text committed to the input field when the key is tapped.
 *               Empty for action keys (backspace, shift, space is handled separately, etc).
 * @param action What special behaviour this key triggers, if any.
 * @param flexWeight Relative width of the key within its row (1f = normal key width).
 */
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
    SWITCH_TO_LETTERS
}

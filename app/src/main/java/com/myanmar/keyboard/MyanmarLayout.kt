package com.myanmar.keyboard

/**
 * Static key-layout tables for the Myanmar Unicode keyboard.
 *
 * The letter mapping follows the official Microsoft "Myanmar (Phonetic order)"
 * layout (KBDMYAN.DLL), which is the same key arrangement used by the widely
 * adopted Myanmar3-style Unicode keyboards.
 */
object MyanmarLayout {

    // ---------- Letters: normal (unshifted) ----------
    val lettersRow1 = listOf(
        KeyModel("ၐ"), KeyModel("၁"), KeyModel("၂"), KeyModel("၃"), KeyModel("၄"),
        KeyModel("၅"), KeyModel("၆"), KeyModel("၇"), KeyModel("၈"), KeyModel("၉"),
        KeyModel("၀"), KeyModel("-"), KeyModel("=")
    )
    val lettersRow2 = listOf(
        KeyModel("ဆ"), KeyModel("တ"), KeyModel("န"), KeyModel("မ"), KeyModel("အ"),
        KeyModel("ပ"), KeyModel("က"), KeyModel("င"), KeyModel("သ"), KeyModel("စ"),
        KeyModel("ဟ"), KeyModel("ဩ")
    )
    val lettersRow3 = listOf(
        KeyModel("ေ"), KeyModel("ျ"), KeyModel("ိ"), KeyModel("်"), KeyModel("ါ"),
        KeyModel("့"), KeyModel("ြ"), KeyModel("ု"), KeyModel("ူ"), KeyModel("း"),
        KeyModel("'"), KeyModel("၏")
    )
    val lettersRow4 = listOf(
        KeyModel("ဖ"), KeyModel("ထ"), KeyModel("ခ"), KeyModel("လ"), KeyModel("ဘ"),
        KeyModel("ည"), KeyModel("ာ"), KeyModel("၊"), KeyModel("။"), KeyModel("/")
    )

    // ---------- Letters: shifted ----------
    val lettersRow1Shift = listOf(
        KeyModel("ဎ"), KeyModel("ဍ"), KeyModel("ၒ"), KeyModel("ဋ"), KeyModel("ၓ"),
        KeyModel("ၔ"), KeyModel("ၕ"), KeyModel("ရ"), KeyModel("*"), KeyModel("("),
        KeyModel(")"), KeyModel("_"), KeyModel("+")
    )
    val lettersRow2Shift = listOf(
        KeyModel("ဈ"), KeyModel("ဝ"), KeyModel("ဣ"), KeyModel("၎"), KeyModel("ဤ"),
        KeyModel("၌"), KeyModel("ဥ"), KeyModel("၍"), KeyModel("ဿ"), KeyModel("ဏ"),
        KeyModel("ဧ"), KeyModel("ဪ")
    )
    val lettersRow3Shift = listOf(
        KeyModel("ဗ"), KeyModel("ှ"), KeyModel("ီ"), KeyModel("္"), KeyModel("ွ"),
        KeyModel("ံ"), KeyModel("ဲ"), KeyModel("ဒ"), KeyModel("ဓ"), KeyModel("ဂ"),
        KeyModel("\""), KeyModel("ၑ")
    )
    val lettersRow4Shift = listOf(
        KeyModel("ဇ"), KeyModel("ဌ"), KeyModel("ဃ"), KeyModel("ဠ"), KeyModel("ယ"),
        KeyModel("ဉ"), KeyModel("ဦ"), KeyModel("?"), KeyModel("!"), KeyModel("\\")
    )

    // ---------- Numbers layer ----------
    val numbersRow1 = listOf(
        KeyModel("၁"), KeyModel("၂"), KeyModel("၃"), KeyModel("၄"), KeyModel("၅"),
        KeyModel("၆"), KeyModel("၇"), KeyModel("၈"), KeyModel("၉"), KeyModel("၀")
    )
    val numbersRow2 = listOf(
        KeyModel("@"), KeyModel("#"), KeyModel("%"), KeyModel("&"), KeyModel("-"),
        KeyModel("+"), KeyModel("("), KeyModel(")"), KeyModel("/")
    )
    val numbersRow3 = listOf(
        KeyModel("*"), KeyModel("\""), KeyModel("'"), KeyModel(":"), KeyModel(";"),
        KeyModel("!"), KeyModel("?"), KeyModel("၊"), KeyModel("။")
    )

    // ---------- Symbols layer ----------
    val symbolsRow1 = listOf(
        KeyModel("["), KeyModel("]"), KeyModel("{"), KeyModel("}"), KeyModel("#"),
        KeyModel("%"), KeyModel("^"), KeyModel("*"), KeyModel("+"), KeyModel("=")
    )
    val symbolsRow2 = listOf(
        KeyModel("_"), KeyModel("\\"), KeyModel("|"), KeyModel("~"), KeyModel("<"),
        KeyModel(">"), KeyModel("$"), KeyModel("€"), KeyModel("¥")
    )
    val symbolsRow3 = listOf(
        KeyModel("."), KeyModel(","), KeyModel("?"), KeyModel("!"), KeyModel("'"),
        KeyModel("\""), KeyModel(":"), KeyModel(";"), KeyModel("/")
    )
}

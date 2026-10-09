package com.practicum.playlistmaker

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// С targetSdk 35+ Android рисует экран под статус-баром и навигацией (edge-to-edge).
// Добавляем к отступам корневого View высоту системных панелей, сохраняя отступы из разметки.
fun View.applySystemBarsPadding() {
    val start = paddingLeft
    val top = paddingTop
    val end = paddingRight
    val bottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        view.setPadding(start + bars.left, top + bars.top, end + bars.right, bottom + bars.bottom)
        insets
    }
}

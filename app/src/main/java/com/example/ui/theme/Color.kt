package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object GankColors {
    var isDark: Boolean by mutableStateOf(false)

    val Ink get() = if (isDark) Color(0xFFF2F2F0) else Color(0xFF0A0A0A)
    val Paper get() = if (isDark) Color(0xFF121212) else Color(0xFFF2F2F0)
    val White get() = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
    val Silver get() = if (isDark) Color(0xFF4A4A4A) else Color(0xFFC9C9C9)
    val Steel get() = if (isDark) Color(0xFFAAAAAA) else Color(0xFF4A4A4A)
    val GankYellow = Color(0xFFFFD400) // aksen utama, dipakai untuk CTA & highlight
    val Green      = Color(0xFF00C853) // Status sukses/selesai
    val Red        = Color(0xFFFF3B30) // Status error/pending
    val Blue       = Color(0xFF00E5FF) // Status proses/diagnosa
}

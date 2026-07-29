package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamically update the GankColors.isDark backing state
    GankColors.isDark = darkTheme

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = GankColors.GankYellow,
            secondary = GankColors.Silver,
            tertiary = GankColors.Steel,
            background = GankColors.Paper,
            surface = GankColors.White,
            onPrimary = GankColors.Ink,
            onSecondary = GankColors.Ink,
            onTertiary = GankColors.White,
            onBackground = GankColors.Ink,
            onSurface = GankColors.Ink
        )
    } else {
        lightColorScheme(
            primary = GankColors.GankYellow,
            secondary = GankColors.Silver,
            tertiary = GankColors.Steel,
            background = GankColors.Paper,
            surface = GankColors.White,
            onPrimary = GankColors.Ink,
            onSecondary = GankColors.Ink,
            onTertiary = GankColors.White,
            onBackground = GankColors.Ink,
            onSurface = GankColors.Ink
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

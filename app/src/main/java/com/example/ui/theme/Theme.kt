package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GankColorScheme = lightColorScheme(
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

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GankColorScheme,
        typography = Typography,
        content = content
    )
}

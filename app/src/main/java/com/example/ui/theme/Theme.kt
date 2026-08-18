package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ElegantDarkColorScheme = darkColorScheme(
    primary = ElegantPrimaryLavender,
    onPrimary = ElegantPrimaryDark,
    primaryContainer = ElegantPrimaryDark,
    onPrimaryContainer = ElegantAccentLight,
    secondary = ElegantAccentLight,
    onSecondary = ElegantPrimaryDark,
    secondaryContainer = ElegantBorder,
    onSecondaryContainer = ElegantTextPrimary,
    tertiary = ElegantCoral,
    onTertiary = Color(0xFF680016),
    background = ElegantDarkBg,
    onBackground = ElegantTextPrimary,
    surface = ElegantCardBg,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantBorder,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantBorder,
    outlineVariant = ElegantBorderSubtle
)

@Composable
fun ChessRoyaleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElegantDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ChessRoyaleTheme(content = content)
}


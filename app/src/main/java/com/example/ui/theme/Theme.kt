package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.ROYAL_EMERALD,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = ThemeFactory.getColorScheme(appTheme)
    val typography = ThemeFactory.getTypography(appTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

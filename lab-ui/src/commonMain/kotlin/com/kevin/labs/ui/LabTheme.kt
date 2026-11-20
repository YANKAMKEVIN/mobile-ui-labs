package com.kevin.labs.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Material 3 theme on the shared paper background, light or dark. Each lab passes its own
 * [primary] and [secondary], which tint the stock Material components (buttons, progress,
 * sliders...).
 *
 * @param darkTheme follows the system setting by default; a nested LabTheme (each lab inside the
 * app) keeps the choice of the enclosing one.
 */
@Composable
fun LabTheme(
    primary: Color,
    secondary: Color,
    darkTheme: Boolean = LocalLabPalette.current?.isDark ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkLabPalette else LightLabPalette
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            secondary = secondary,
            onSecondary = Color.White,
            background = palette.paper,
            onBackground = palette.ink,
            surface = palette.card,
            onSurface = palette.ink,
            onSurfaceVariant = palette.muted,
            outline = palette.line,
            outlineVariant = palette.line,
            surfaceContainerLow = palette.card,
            surfaceContainer = palette.card,
            surfaceContainerHigh = palette.card,
            inverseSurface = palette.inverse,
            inverseOnSurface = palette.onInverse
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            background = palette.paper,
            surface = palette.card
        )
    }
    CompositionLocalProvider(LocalLabPalette provides palette) {
        MaterialTheme(colorScheme = colors) {
            Surface(modifier = Modifier.fillMaxSize(), color = palette.paper, content = content)
        }
    }
}

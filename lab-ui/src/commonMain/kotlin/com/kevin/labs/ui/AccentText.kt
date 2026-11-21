package com.kevin.labs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

// The labs' accents are dark, saturated colours picked for the paper theme: as text on the night
// cards they fall to about 1.5:1 to 3.9:1. Text drawn in an accent goes through accentText(),
// which keeps the hue but lightens it at night until it is readable.

/** Minimum WCAG contrast for body text (AA). */
const val TEXT_CONTRAST = 4.5f

/** WCAG contrast ratio between two opaque colours, from 1 (same) to 21 (black on white). */
fun contrastRatio(a: Color, b: Color): Float {
    val la = a.luminance()
    val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * [accent] itself when it already reaches [minRatio] on [background], otherwise the closest mix
 * of [accent] with white (on a dark background) or black (on a light one) that does.
 */
fun readableOn(accent: Color, background: Color, minRatio: Float = TEXT_CONTRAST): Color {
    if (contrastRatio(accent, background) >= minRatio) return accent
    val target = if (background.luminance() < 0.5f) Color.White else Color.Black
    var step = 1
    while (step <= 20) {
        val mixed = lerp(accent, target, step / 20f)
        if (contrastRatio(mixed, background) >= minRatio) return mixed
        step++
    }
    return target
}

/**
 * An accent used as text or icon colour on the paper or on a card: unchanged by day, lightened
 * at night so that it stays readable on the night cards (the lighter of the two night surfaces).
 */
val Color.asText: Color
    @Composable @ReadOnlyComposable get() {
        val palette = LocalLabPalette.current ?: LightLabPalette
        return if (palette.isDark) readableOn(this, palette.card) else this
    }

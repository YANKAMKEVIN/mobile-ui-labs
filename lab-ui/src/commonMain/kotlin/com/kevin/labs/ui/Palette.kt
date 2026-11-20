package com.kevin.labs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Neutral palette shared by every lab, in a light (paper) and a dark (night paper) version picked
// by LabTheme. Accent colours stay in each lab: they carry meaning there (one per pattern /
// state), so they are data, not theme.

/** The neutral colours of one theme variant. */
class LabPalette(
    val isDark: Boolean,
    /** Background of every screen. */
    val paper: Color,
    /** Primary text. */
    val ink: Color,
    /** Secondary text. */
    val muted: Color,
    /** Hairline borders, dividers and placeholder shapes. */
    val line: Color,
    /** Cards, pills and panels laid on the paper. */
    val card: Color,
    /** High-contrast surfaces: the rule card, snackbars. Dark on paper, light at night. */
    val inverse: Color,
    /** Titles on [inverse]. */
    val onInverse: Color,
    /** Body text on [inverse]. */
    val onInverseMuted: Color,
    /** Action label on [inverse] (snackbar "UNDO"...). */
    val inverseAction: Color
)

val LightLabPalette = LabPalette(
    isDark = false,
    paper = Color(0xFFF5F0E8),
    ink = Color(0xFF111713),
    muted = Color(0xFF667068),
    line = Color(0xFFE0D6C8),
    card = Color.White,
    inverse = Color(0xFF111713),
    onInverse = Color.White,
    onInverseMuted = Color(0xFFECE7DC),
    inverseAction = Color(0xFFFFD166)
)

val DarkLabPalette = LabPalette(
    isDark = true,
    paper = Color(0xFF151916),
    ink = Color(0xFFECE7DC),
    muted = Color(0xFFA2ABA4),
    line = Color(0xFF353D37),
    card = Color(0xFF1F2621),
    inverse = Color(0xFFECE7DC),
    onInverse = Color(0xFF111713),
    onInverseMuted = Color(0xFF3B443E),
    inverseAction = Color(0xFF8A5A00)
)

/** Palette of the enclosing [LabTheme]; null outside of any. */
val LocalLabPalette = staticCompositionLocalOf<LabPalette?> { null }

private val palette: LabPalette
    @Composable @ReadOnlyComposable get() = LocalLabPalette.current ?: LightLabPalette

/** Background of every screen. */
val Paper: Color @Composable @ReadOnlyComposable get() = palette.paper

/** Primary text. */
val Ink: Color @Composable @ReadOnlyComposable get() = palette.ink

/** Secondary text. */
val Muted: Color @Composable @ReadOnlyComposable get() = palette.muted

/** Hairline borders and dividers. */
val Line: Color @Composable @ReadOnlyComposable get() = palette.line

/** Cards, pills and panels laid on the paper (white by day). */
val CardColor: Color @Composable @ReadOnlyComposable get() = palette.card

/** High-contrast surface of the rule card and snackbars. */
val Inverse: Color @Composable @ReadOnlyComposable get() = palette.inverse

/** Titles on [Inverse]. */
val OnInverse: Color @Composable @ReadOnlyComposable get() = palette.onInverse

/** Body text on [Inverse]. */
val OnInverseMuted: Color @Composable @ReadOnlyComposable get() = palette.onInverseMuted

/** Action label on [Inverse]. */
val InverseAction: Color @Composable @ReadOnlyComposable get() = palette.inverseAction

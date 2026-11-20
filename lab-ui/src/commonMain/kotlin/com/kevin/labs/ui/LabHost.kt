package com.kevin.labs.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * True when a lab runs inside the app shell, whose top bar already shows the lab title next to
 * Back. [LabHeader] then drops its big title and keeps a one-line, expandable subtitle, so the
 * pattern tabs and the demo start near the top of the screen.
 */
val LocalLabHosted = staticCompositionLocalOf { false }

/**
 * What the app shell shows at the very end of a lab (links to the previous and next labs); null
 * when the lab runs on its own. Each lab places it with [LabFooter] after its rule card.
 */
val LocalLabFooter = staticCompositionLocalOf<(@Composable () -> Unit)?> { null }

/** The shell's end-of-lab links ([LocalLabFooter]), or nothing outside the shell. */
@Composable
fun LabFooter(modifier: Modifier = Modifier) {
    val footer = LocalLabFooter.current ?: return
    Box(modifier) { footer() }
}

package com.kevin.labs.ui

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * True when a lab runs inside the app shell, whose top bar already shows the lab title next to
 * Back. [LabHeader] then drops its big title and keeps a one-line, expandable subtitle, so the
 * pattern tabs and the demo start near the top of the screen.
 */
val LocalLabHosted = staticCompositionLocalOf { false }

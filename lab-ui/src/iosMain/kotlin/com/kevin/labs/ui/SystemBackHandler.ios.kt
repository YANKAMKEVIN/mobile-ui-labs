package com.kevin.labs.ui

import androidx.compose.runtime.Composable

/** iOS has no system Back action: on-screen Back buttons are the only way back. */
@Composable
actual fun SystemBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

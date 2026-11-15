package com.kevin.labs

import androidx.compose.runtime.Composable

/** iOS has no system Back action: the on-screen "All labs" button is the way back. */
@Composable
actual fun SystemBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

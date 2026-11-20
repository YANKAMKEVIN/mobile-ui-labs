package com.kevin.labs.ui

import androidx.compose.runtime.Composable

/**
 * Like [SystemBackHandler], but follows the Android predictive back gesture: [onProgress] gets
 * 0..1 while the finger moves, then either [onBack] (gesture committed, or a plain Back press) or
 * [onCancel] (finger released early) is called.
 */
@Composable
expect fun PredictiveBackHandler(
    enabled: Boolean,
    onProgress: (Float) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
)

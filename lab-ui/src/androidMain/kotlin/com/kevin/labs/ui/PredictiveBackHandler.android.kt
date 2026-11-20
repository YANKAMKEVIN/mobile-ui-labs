package com.kevin.labs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlin.coroutines.cancellation.CancellationException
import androidx.activity.compose.PredictiveBackHandler as ActivityPredictiveBackHandler

@Composable
actual fun PredictiveBackHandler(
    enabled: Boolean,
    onProgress: (Float) -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val progress by rememberUpdatedState(onProgress)
    val cancel by rememberUpdatedState(onCancel)
    val back by rememberUpdatedState(onBack)
    ActivityPredictiveBackHandler(enabled = enabled) { events ->
        try {
            events.collect { progress(it.progress) }
            back()
        } catch (e: CancellationException) {
            cancel()
            throw e
        }
    }
}

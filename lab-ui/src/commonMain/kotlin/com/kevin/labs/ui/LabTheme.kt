package com.kevin.labs.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Material 3 light theme on the shared paper background. Each lab passes its own [primary] and
 * [secondary], which tint the stock Material components (buttons, progress, sliders...).
 */
@Composable
fun LabTheme(
    primary: Color,
    secondary: Color,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = primary,
            secondary = secondary,
            background = Paper,
            surface = Color.White
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Paper, content = content)
    }
}

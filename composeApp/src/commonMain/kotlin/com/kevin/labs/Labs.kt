package com.kevin.labs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** One entry of the catalog: a self-contained lab screen. */
class Lab(
    val id: String,
    val title: String,
    val summary: String,
    val accent: Color,
    val content: @Composable () -> Unit
)

/** The series, in the order shown on the home screen. */
val labs: List<Lab> = listOf()

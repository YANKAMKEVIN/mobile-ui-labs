package com.kevin.labs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kevin.adaptivelayoutlab.App as AdaptiveLayoutLab
import com.kevin.bottomsheetplaybook.App as BottomSheetPlaybook

/** One entry of the catalog: a self-contained lab screen. */
class Lab(
    val id: String,
    val title: String,
    val summary: String,
    val accent: Color,
    val content: @Composable () -> Unit
)

/** The series, in the order shown on the home screen. */
val labs: List<Lab> = listOf(
    Lab(
        id = "adaptive-layout-lab",
        title = "Adaptive Layout Lab",
        summary = "Change hierarchy, not just width: compact, medium, expanded, foldable, dense.",
        accent = Color(0xFF0E6A59),
        content = { AdaptiveLayoutLab() }
    ),
    Lab(
        id = "bottom-sheet-playbook",
        title = "Bottom Sheet Playbook",
        summary = "Pick the right sheet for confirm, filters, peek, forms and actions.",
        accent = Color(0xFF0F6B55),
        content = { BottomSheetPlaybook() }
    ),
)

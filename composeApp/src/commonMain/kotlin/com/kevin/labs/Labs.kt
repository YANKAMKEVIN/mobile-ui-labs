package com.kevin.labs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kevin.adaptivelayoutlab.App as AdaptiveLayoutLab
import com.kevin.bottomsheetplaybook.App as BottomSheetPlaybook
import com.kevin.emptystategallery.App as EmptyStateGallery
import com.kevin.feedbacksurfacelab.App as FeedbackSurfaceLab
import com.kevin.loadinguxpatterns.App as LoadingUxPatterns
import com.kevin.navigationbackstacklab.App as NavigationBackstackLab
import com.kevin.offlinefirstnotes.App as OfflineFirstNotes
import com.kevin.permissiononboardinglab.App as PermissionOnboardingLab
import com.kevin.searchstatemachine.App as SearchStateMachine

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
    Lab(
        id = "empty-state-gallery",
        title = "Empty State Gallery",
        summary = "First use, no results, offline, permission, error: what to say and do.",
        accent = Color(0xFF1B5E4A),
        content = { EmptyStateGallery() }
    ),
    Lab(
        id = "feedback-surface-lab",
        title = "Feedback Surface Lab",
        summary = "Snackbar, undo, inline error, banner or blocking dialog: where feedback lives.",
        accent = Color(0xFF0F6B55),
        content = { FeedbackSurfaceLab() }
    ),
    Lab(
        id = "loading-ux-patterns",
        title = "Loading UX Patterns",
        summary = "Inline spinner, skeleton, steps, optimistic UI: progress after an action.",
        accent = Color(0xFF0E6251),
        content = { LoadingUxPatterns() }
    ),
    Lab(
        id = "navigation-backstack-lab",
        title = "Navigation Backstack Lab",
        summary = "A real back stack: tabs, nested routes, modal layer, deep links and Back.",
        accent = Color(0xFF2864A8),
        content = { NavigationBackstackLab() }
    ),
    Lab(
        id = "offline-first-notes",
        title = "Offline First Notes",
        summary = "Local-first writing, sync queue, background progress and conflicts.",
        accent = Color(0xFF0D6B57),
        content = { OfflineFirstNotes() }
    ),
    Lab(
        id = "permission-onboarding-lab",
        title = "Permission Onboarding Lab",
        summary = "Ask with context, handle denial, limited access and the Settings detour.",
        accent = Color(0xFF0E7059),
        content = { PermissionOnboardingLab() }
    ),
    Lab(
        id = "search-state-machine",
        title = "Search State Machine",
        summary = "A tested reducer: debounce, loading, results, empty, error and retry.",
        accent = Color(0xFF245FA8),
        content = { SearchStateMachine() }
    ),
)

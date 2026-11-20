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
import com.kevin.swipeactionlab.App as SwipeActionLab

/**
 * One entry of the catalog: a self-contained lab screen. [accent] tints its [vignette], the small
 * drawing shown on the home screen.
 */
class Lab(
    val id: String,
    val title: String,
    val summary: String,
    val accent: Color,
    val vignette: Vignette,
    val content: @Composable () -> Unit
)

/** The series, in the order shown on the home screen. */
val labs: List<Lab> = listOf(
    Lab(
        id = "adaptive-layout-lab",
        title = "Adaptive Layout Lab",
        summary = "Change hierarchy, not just width: compact, medium, expanded, foldable, dense.",
        accent = Color(0xFF0E6A59),
        vignette = Vignette.Adaptive,
        content = { AdaptiveLayoutLab() }
    ),
    Lab(
        id = "bottom-sheet-playbook",
        title = "Bottom Sheet Playbook",
        summary = "Pick the right sheet for confirm, filters, peek, forms and actions.",
        accent = Color(0xFFB5532A),
        vignette = Vignette.Sheet,
        content = { BottomSheetPlaybook() }
    ),
    Lab(
        id = "empty-state-gallery",
        title = "Empty State Gallery",
        summary = "First use, no results, offline, permission, error: what to say and do.",
        accent = Color(0xFF6750A4),
        vignette = Vignette.Empty,
        content = { EmptyStateGallery() }
    ),
    Lab(
        id = "feedback-surface-lab",
        title = "Feedback Surface Lab",
        summary = "Snackbar, undo, inline error, banner or blocking dialog: where feedback lives.",
        accent = Color(0xFFB86812),
        vignette = Vignette.Feedback,
        content = { FeedbackSurfaceLab() }
    ),
    Lab(
        id = "loading-ux-patterns",
        title = "Loading UX Patterns",
        summary = "Inline spinner, skeleton, steps, optimistic UI: progress after an action.",
        accent = Color(0xFF2F5DA8),
        vignette = Vignette.Loading,
        content = { LoadingUxPatterns() }
    ),
    Lab(
        id = "navigation-backstack-lab",
        title = "Navigation Backstack Lab",
        summary = "A real back stack: tabs, nested routes, modal layer, deep links and Back.",
        accent = Color(0xFF6B57B8),
        vignette = Vignette.Backstack,
        content = { NavigationBackstackLab() }
    ),
    Lab(
        id = "offline-first-notes",
        title = "Offline First Notes",
        summary = "Local-first writing, sync queue, background progress and conflicts.",
        accent = Color(0xFFB76F16),
        vignette = Vignette.Offline,
        content = { OfflineFirstNotes() }
    ),
    Lab(
        id = "permission-onboarding-lab",
        title = "Permission Onboarding Lab",
        summary = "Ask with context, handle denial, limited access and the Settings detour.",
        accent = Color(0xFF0E7059),
        vignette = Vignette.Permission,
        content = { PermissionOnboardingLab() }
    ),
    Lab(
        id = "search-state-machine",
        title = "Search State Machine",
        summary = "A tested reducer: debounce, loading, results, empty, error and retry.",
        accent = Color(0xFF245FA8),
        vignette = Vignette.Search,
        content = { SearchStateMachine() }
    ),
    Lab(
        id = "swipe-action-lab",
        title = "Swipe Action Lab",
        summary = "Swipe actions that scroll, resolve and undo, without accidental triggers.",
        accent = Color(0xFFD33F49),
        vignette = Vignette.Swipe,
        content = { SwipeActionLab() }
    ),
)

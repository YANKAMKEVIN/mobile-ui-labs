package com.kevin.labs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kevin.adaptivelayoutlab.App as AdaptiveLayoutLab
import com.kevin.adaptivelayoutlab.PITCH as AdaptiveLayoutPitch
import com.kevin.bottomsheetplaybook.App as BottomSheetPlaybook
import com.kevin.bottomsheetplaybook.PITCH as BottomSheetPitch
import com.kevin.emptystategallery.App as EmptyStateGallery
import com.kevin.emptystategallery.PITCH as EmptyStatePitch
import com.kevin.feedbacksurfacelab.App as FeedbackSurfaceLab
import com.kevin.feedbacksurfacelab.PITCH as FeedbackSurfacePitch
import com.kevin.loadinguxpatterns.App as LoadingUxPatterns
import com.kevin.loadinguxpatterns.PITCH as LoadingPitch
import com.kevin.navigationbackstacklab.App as NavigationBackstackLab
import com.kevin.navigationbackstacklab.PITCH as NavigationPitch
import com.kevin.offlinefirstnotes.App as OfflineFirstNotes
import com.kevin.offlinefirstnotes.PITCH as OfflineFirstPitch
import com.kevin.permissiononboardinglab.App as PermissionOnboardingLab
import com.kevin.permissiononboardinglab.PITCH as PermissionPitch
import com.kevin.searchstatemachine.App as SearchStateMachine
import com.kevin.searchstatemachine.PITCH as SearchPitch
import com.kevin.swipeactionlab.App as SwipeActionLab
import com.kevin.swipeactionlab.PITCH as SwipePitch

/** The three themes the catalog is grouped under, in order. */
enum class LabGroup(val title: String) {
    Structure("Structure & navigation"),
    Feedback("Feedback & states"),
    Data("Data & permissions")
}

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
    val group: LabGroup,
    /** Name of the private composable that draws the lab, shown with the "View code" link. */
    val mainComposable: String,
    val content: @Composable () -> Unit
)

/** The series, grouped by [LabGroup] and in the order (and numbering) shown on the home screen. */
val labs: List<Lab> = listOf(
    Lab(
        id = "adaptive-layout-lab",
        title = "Adaptive Layout Lab",
        summary = AdaptiveLayoutPitch,
        accent = Color(0xFF0E6A59),
        vignette = Vignette.Adaptive,
        group = LabGroup.Structure,
        mainComposable = "AdaptiveLayoutLab",
        content = { AdaptiveLayoutLab() }
    ),
    Lab(
        id = "navigation-backstack-lab",
        title = "Navigation Backstack Lab",
        summary = NavigationPitch,
        accent = Color(0xFF6B57B8),
        vignette = Vignette.Backstack,
        group = LabGroup.Structure,
        mainComposable = "NavigationBackstackLab",
        content = { NavigationBackstackLab() }
    ),
    Lab(
        id = "bottom-sheet-playbook",
        title = "Bottom Sheet Playbook",
        summary = BottomSheetPitch,
        accent = Color(0xFFB5532A),
        vignette = Vignette.Sheet,
        group = LabGroup.Structure,
        mainComposable = "BottomSheetPlaybook",
        content = { BottomSheetPlaybook() }
    ),
    Lab(
        id = "swipe-action-lab",
        title = "Swipe Action Lab",
        summary = SwipePitch,
        accent = Color(0xFFD33F49),
        vignette = Vignette.Swipe,
        group = LabGroup.Structure,
        mainComposable = "SwipeActionLab",
        content = { SwipeActionLab() }
    ),
    Lab(
        id = "feedback-surface-lab",
        title = "Feedback Surface Lab",
        summary = FeedbackSurfacePitch,
        accent = Color(0xFFB86812),
        vignette = Vignette.Feedback,
        group = LabGroup.Feedback,
        mainComposable = "FeedbackSurfaceLab",
        content = { FeedbackSurfaceLab() }
    ),
    Lab(
        id = "loading-ux-patterns",
        title = "Loading UX Patterns",
        summary = LoadingPitch,
        accent = Color(0xFF2F5DA8),
        vignette = Vignette.Loading,
        group = LabGroup.Feedback,
        mainComposable = "LoadingGallery",
        content = { LoadingUxPatterns() }
    ),
    Lab(
        id = "empty-state-gallery",
        title = "Empty State Gallery",
        summary = EmptyStatePitch,
        accent = Color(0xFF6750A4),
        vignette = Vignette.Empty,
        group = LabGroup.Feedback,
        mainComposable = "EmptyStateGallery",
        content = { EmptyStateGallery() }
    ),
    Lab(
        id = "search-state-machine",
        title = "Search State Machine",
        summary = SearchPitch,
        accent = Color(0xFF245FA8),
        vignette = Vignette.Search,
        group = LabGroup.Feedback,
        mainComposable = "SearchStateMachine",
        content = { SearchStateMachine() }
    ),
    Lab(
        id = "offline-first-notes",
        title = "Offline First Notes",
        summary = OfflineFirstPitch,
        accent = Color(0xFFB76F16),
        vignette = Vignette.Offline,
        group = LabGroup.Data,
        mainComposable = "OfflineFirstNotes",
        content = { OfflineFirstNotes() }
    ),
    Lab(
        id = "permission-onboarding-lab",
        title = "Permission Onboarding Lab",
        summary = PermissionPitch,
        accent = Color(0xFF0E7059),
        vignette = Vignette.Permission,
        group = LabGroup.Data,
        mainComposable = "PermissionOnboardingLab",
        content = { PermissionOnboardingLab() }
    ),
)

/** Public repository of the series, where "View code" leads. */
const val REPOSITORY_URL = "https://github.com/YANKAMKEVIN/mobile-ui-labs"

/** The lab's source file on the default branch: each lab lives in com.kevin.<id without dashes>. */
fun Lab.sourceUrl(): String =
    "$REPOSITORY_URL/blob/main/composeApp/src/commonMain/kotlin/com/kevin/${id.replace("-", "")}/App.kt"

/** Two-digit number of a lab in the series ("01" to "10"). */
fun Lab.number(): String = (labs.indexOf(this) + 1).toString().padStart(2, '0')

/** The labs under each group, in catalog order. */
fun labsByGroup(): List<Pair<LabGroup, List<Lab>>> =
    LabGroup.entries.map { group -> group to labs.filter { it.group == group } }

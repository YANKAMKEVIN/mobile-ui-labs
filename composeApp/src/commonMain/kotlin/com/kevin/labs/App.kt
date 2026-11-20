package com.kevin.labs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.LocalLabFooter
import com.kevin.labs.ui.LocalLabHosted
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.PredictiveBackHandler
import kotlinx.coroutines.flow.first

/** The whole app; [darkTheme] follows the system setting unless forced. */
@Composable
fun App(darkTheme: Boolean = isSystemInDarkTheme()) {
    LabTheme(primary = Ink, secondary = Muted, darkTheme = darkTheme) {
        // The open lab is saved by id, so rotation or process death brings the user back to it.
        var openId by rememberSaveable { mutableStateOf<String?>(null) }
        // 0..1 while an Android predictive back gesture pulls the open lab away.
        var backProgress by remember { mutableFloatStateOf(0f) }
        // Kept here, above the catalog, so the list is back where it was when a lab closes.
        val catalogState = rememberLazyGridState()
        // Labs opened during this session (and the last one), marked on the catalog.
        var visited by rememberSaveable(stateSaver = VisitedSaver) { mutableStateOf(emptySet<String>()) }
        var lastOpened by rememberSaveable { mutableStateOf<String?>(null) }
        val openLab: (Lab) -> Unit = {
            backProgress = 0f
            openId = it.id
            visited = visited + it.id
            lastOpened = it.id
        }
        AnimatedContent(
            targetState = openId,
            transitionSpec = {
                // Deeper in the catalog order slides in from the end, going back slides out to it.
                if (labOrder(targetState) > labOrder(initialState)) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 4 } + fadeOut())
                } else {
                    (slideInHorizontally { -it / 4 } + fadeIn()) togetherWith
                        (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "lab-navigation"
        ) { id ->
            val open = labs.firstOrNull { it.id == id }
            if (open == null) {
                Catalog(onOpen = openLab, state = catalogState, visited = visited, lastOpened = lastOpened)
            } else {
                LabScreen(
                    lab = open,
                    onBack = { openId = null },
                    onOpenLab = openLab,
                    backProgress = backProgress,
                    onBackProgress = { backProgress = it }
                )
            }
        }
    }
}

/** Position of a lab in the catalog; the catalog itself (null) comes first. */
internal fun labOrder(id: String?): Int = labs.indexOfFirst { it.id == id }

@Composable
fun Catalog(
    onOpen: (Lab) -> Unit,
    state: LazyGridState = rememberLazyGridState(),
    visited: Set<String> = emptySet(),
    lastOpened: String? = null
) {
    // Coming back from a lab reached through the previous / next links: bring it into view.
    LaunchedEffect(lastOpened) {
        val id = lastOpened ?: return@LaunchedEffect
        snapshotFlow { state.layoutInfo.totalItemsCount }.first { it > 0 } // wait for the first layout
        if (state.layoutInfo.visibleItemsInfo.none { it.key == id }) {
            state.scrollToItem(catalogItemIndex(id))
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        // One column on phones, a grid on tablets and in landscape, never wider than is readable.
        val columns = catalogColumns(maxWidth.value)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = state,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = MAX_CATALOG_WIDTH.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LabHeader(
                    title = "Mobile UI Labs",
                    subtitle = "Ten small Compose Multiplatform labs, one UX decision each.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            labsByGroup().forEach { (group, groupLabs) ->
                item(key = group.name, span = { GridItemSpan(maxLineSpan) }) { GroupTitle(group) }
                items(groupLabs, key = { it.id }) { lab ->
                    LabRow(lab, visited = lab.id in visited, onClick = { onOpen(lab) })
                }
            }
        }
    }
}

/** Saves the visited lab ids as a list, which every platform can put in saved state. */
private val VisitedSaver = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })

/** Index of a lab's card in the catalog grid: after the header and the group titles before it. */
internal fun catalogItemIndex(id: String): Int {
    var index = 1 // the header
    for ((_, groupLabs) in labsByGroup()) {
        index++ // the group title
        val position = groupLabs.indexOfFirst { it.id == id }
        if (position >= 0) return index + position
        index += groupLabs.size
    }
    return 0
}

/** Widest the catalog grows; beyond, it stays centred with paper on both sides. */
internal const val MAX_CATALOG_WIDTH = 1080

/** Catalog columns for a window [widthDp] wide: 1 under 600 dp, 2 under 840 dp, 3 beyond. */
internal fun catalogColumns(widthDp: Float): Int = when {
    widthDp < 600f -> 1
    widthDp < 840f -> 2
    else -> 3
}

@Composable
private fun GroupTitle(group: LabGroup) {
    Text(
        text = group.title.uppercase(),
        modifier = Modifier
            .padding(top = 12.dp, bottom = 2.dp)
            .semantics { heading() },
        color = Muted,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp,
        letterSpacing = 1.sp
    )
}

@Composable
private fun LabRow(lab: Lab, visited: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardColor,
        border = BorderStroke(1.dp, Line)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabVignette(lab.vignette, lab.accent)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = lab.number(),
                    color = lab.accent,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 0.8.sp
                )
                Text(lab.title, color = Ink, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text(lab.summary, color = Muted, lineHeight = 20.sp, fontSize = 14.sp)
            }
            if (visited) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Opened",
                    tint = lab.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Muted
            )
        }
    }
}

/**
 * A lab under a slim top bar: Back to the catalog and the lab title. The lab's own header then
 * shrinks to a one-line pitch ([LocalLabHosted]). System Back also returns to the catalog, and the
 * screen follows the predictive back gesture through [backProgress]. The lab ends with links to
 * the previous and next labs ([onOpenLab]).
 */
@Composable
fun LabScreen(
    lab: Lab,
    onBack: () -> Unit,
    onOpenLab: (Lab) -> Unit = {},
    backProgress: Float = 0f,
    onBackProgress: (Float) -> Unit = {}
) {
    PredictiveBackHandler(
        enabled = true,
        onProgress = onBackProgress,
        onCancel = { onBackProgress(0f) },
        onBack = onBack
    )
    // During the gesture the lab shrinks a little and follows the finger, revealing where Back leads.
    Column(
        Modifier
            .fillMaxSize()
            .graphicsLayer {
                val scale = 1f - 0.1f * backProgress
                scaleX = scale
                scaleY = scale
                translationX = backProgress * 24.dp.toPx()
                shape = RoundedCornerShape(28.dp * backProgress)
                clip = backProgress > 0f
            }
            .background(Paper)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper)
                .statusBarsPadding()
                .padding(start = 4.dp, end = 16.dp)
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "All labs",
                    tint = Ink
                )
            }
            Text(
                text = lab.title,
                modifier = Modifier.weight(1f).semantics { heading() },
                color = Ink,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        // The bar above already pads for the status bar: labs must not pad for it a second time.
        Box(Modifier.weight(1f).consumeWindowInsets(WindowInsets.statusBars)) {
            CompositionLocalProvider(
                LocalLabHosted provides true,
                LocalLabFooter provides { LabNeighbours(lab, onOpenLab) }
            ) { lab.content() }
        }
    }
}

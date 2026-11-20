package com.kevin.adaptivelayoutlab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.labs.ui.CardColor
import com.kevin.labs.ui.Ink
import com.kevin.labs.ui.LabFooter
import com.kevin.labs.ui.LabHeader
import com.kevin.labs.ui.LabTabs
import com.kevin.labs.ui.LabTheme
import com.kevin.labs.ui.Line
import com.kevin.labs.ui.Muted
import com.kevin.labs.ui.Paper
import com.kevin.labs.ui.RuleCard
import com.kevin.labs.ui.swipeBetweenTabs
import kotlin.math.roundToInt

/** The UX question this lab asks; also its summary on the catalog. */
internal const val PITCH =
    "When should a layout change its hierarchy, not just stretch? Compare compact, medium, expanded, foldable and dense."

@Composable
fun App() {
    LabTheme(primary = Green, secondary = Blue) {
        AdaptiveLayoutLab()
    }
}

/** [widthClass] is set for the layouts that a window width alone decides. */
private enum class LayoutMode(val widthClass: WidthClass?) {
    Compact(WidthClass.Compact),
    Medium(WidthClass.Medium),
    Expanded(WidthClass.Expanded),
    Fold(null),
    Density(null)
}

private data class LayoutCopy(
    val mode: LayoutMode,
    val tab: String,
    val title: String,
    val body: String,
    val rule: String,
    val accent: Color
)

private val Green = Color(0xFF0E6A59)
private val Blue = Color(0xFF2864A8)
private val Coral = Color(0xFFC0523A)
private val Violet = Color(0xFF6F57B4)
private val Slate = Color(0xFF34444B)

private val modes = listOf(
    LayoutCopy(
        mode = LayoutMode.Compact,
        tab = "Compact",
        title = "Prioritize the next action",
        body = "One column, short cards and bottom navigation keep the thumb flow obvious.",
        rule = "Compact screens need a clear path, not every option at once.",
        accent = Green
    ),
    LayoutCopy(
        mode = LayoutMode.Medium,
        tab = "Medium",
        title = "Promote navigation",
        body = "A rail gives destinations more stability while the content still breathes.",
        rule = "Medium width is the moment to separate movement from content.",
        accent = Blue
    ),
    LayoutCopy(
        mode = LayoutMode.Expanded,
        tab = "Expanded",
        title = "Add a detail pane",
        body = "Large screens earn their size when list and detail can stay visible together.",
        rule = "Expanded layouts should reduce navigation cost, not add empty space.",
        accent = Coral
    ),
    LayoutCopy(
        mode = LayoutMode.Fold,
        tab = "Fold",
        title = "Respect physical space",
        body = "A hinge is a product constraint, so content should avoid fighting the fold.",
        rule = "Foldables are not just wide phones; the middle can be a boundary.",
        accent = Violet
    ),
    LayoutCopy(
        mode = LayoutMode.Density,
        tab = "Density",
        title = "Tune the rhythm",
        body = "Dense mode keeps scan speed high without making the interface feel cramped.",
        rule = "Density is a choice about attention, not a shortcut for smaller text.",
        accent = Slate
    )
)

@Composable
private fun AdaptiveLayoutLab() {
    var selectedIndex by rememberSaveable { mutableStateOf(2) }
    val selected = modes[selectedIndex]
    // Width of the simulated window. Dragging it switches the tab at each threshold; picking a
    // size tab moves it to a typical width for that class.
    var windowWidth by rememberSaveable { mutableFloatStateOf(typicalWidth(WidthClass.Expanded)) }
    val selectMode: (Int) -> Unit = { index ->
        selectedIndex = index
        modes[index].mode.widthClass?.let { windowWidth = typicalWidth(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LabHeader(
                title = "Adaptive Layout Lab",
                subtitle = PITCH,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item {
            LabTabs(
                items = modes,
                label = { it.tab },
                accent = { it.accent },
                selectedIndex = selectedIndex,
                onSelected = selectMode,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
        item {
            AnimatedContent(
                targetState = selected,
                modifier = Modifier.swipeBetweenTabs(selectedIndex, modes.size, selectMode),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "layout-mode"
            ) { mode ->
                LayoutPreview(
                    copy = mode,
                    windowFraction = if (mode.mode.widthClass != null) previewFraction(windowWidth) else 1f,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        item {
            WidthControl(
                widthDp = windowWidth,
                onWidthChange = { width ->
                    windowWidth = width
                    val mode = LayoutMode.entries.first { it.widthClass == widthClassOf(width) }
                    selectedIndex = modes.indexOfFirst { it.mode == mode }
                },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        item {
            RuleCard(
                title = "Design rule",
                rule = selected.rule,
                modifier = Modifier.padding(horizontal = 20.dp),
                accent = selected.accent
            )
        }
        item { LabFooter(Modifier.padding(horizontal = 20.dp)) }
        item { Spacer(modifier = Modifier.height(2.dp)) }
    }
}

@Composable
private fun LayoutPreview(copy: LayoutCopy, windowFraction: Float, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = CardColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                WindowChrome(copy, Modifier.fillMaxWidth(windowFraction))
            }
            Text(
                text = copy.title,
                color = Ink,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = copy.body,
                color = Muted,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun WindowChrome(copy: LayoutCopy, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(260.dp),
        shape = RoundedCornerShape(22.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == 0) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) copy.accent else Line)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = copy.tab,
                    color = Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            when (copy.mode) {
                LayoutMode.Compact -> CompactPreview(copy.accent)
                LayoutMode.Medium -> MediumPreview(copy.accent)
                LayoutMode.Expanded -> ExpandedPreview(copy.accent)
                LayoutMode.Fold -> FoldPreview(copy.accent)
                LayoutMode.Density -> DensityPreview(copy.accent)
            }
        }
    }
}

@Composable
private fun CompactPreview(accent: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HeroBlock(accent, Modifier.fillMaxWidth().height(58.dp))
        repeat(3) { index ->
            ContentRow(
                accent = if (index == 0) accent else Line,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(CardColor)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == 0) 20.dp else 14.dp)
                        .clip(CircleShape)
                        .background(if (index == 0) accent else Line)
                )
            }
        }
    }
}

@Composable
private fun MediumPreview(accent: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Rail(accent)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeroBlock(accent, Modifier.fillMaxWidth().height(64.dp))
            repeat(4) { index ->
                ContentRow(
                    accent = if (index == 1) accent else Line,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ExpandedPreview(accent: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Rail(accent)
        Column(
            modifier = Modifier.weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(5) { index ->
                ContentRow(
                    accent = if (index == 2) accent else Line,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        DetailPane(accent, Modifier.weight(1.25f))
    }
}

@Composable
private fun FoldPreview(accent: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeroBlock(accent, Modifier.fillMaxWidth().height(58.dp))
            repeat(3) { ContentRow(Line, Modifier.fillMaxWidth()) }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFD1C5B6))
        )
        DetailPane(accent, Modifier.weight(1f))
    }
}

@Composable
private fun DensityPreview(accent: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(3) { index ->
                MetricChip(
                    active = index == 0,
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        repeat(7) { index ->
            DenseRow(
                accent = if (index == 1) accent else Line,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Rail(accent: Color) {
    Column(
        modifier = Modifier
            .width(44.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == 0) 22.dp else 16.dp)
                    .clip(CircleShape)
                    .background(if (index == 0) accent else Line)
            )
        }
    }
}

@Composable
private fun HeroBlock(accent: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(accent.copy(alpha = 0.14f))
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accent)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardColor)
        )
    }
}

@Composable
private fun ContentRow(accent: Color, modifier: Modifier) {
    Row(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Line)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.46f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Line.copy(alpha = 0.68f))
            )
        }
    }
}

@Composable
private fun DetailPane(accent: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(CardColor)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accent.copy(alpha = 0.18f))
        )
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (index == 2) 0.56f else 0.86f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (index == 0) accent else Line)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(50),
            color = accent
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                text = "Detail",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MetricChip(active: Boolean, accent: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) accent else CardColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.52f)
                .height(7.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(if (active) Color.White else Line)
        )
    }
}

@Composable
private fun DenseRow(accent: Color, modifier: Modifier) {
    Row(
        modifier = modifier
            .height(25.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CardColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Line)
        )
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Line.copy(alpha = 0.72f))
        )
    }
}

/** Slider that resizes the simulated window; the layout follows the width class it falls in. */
@Composable
private fun WidthControl(widthDp: Float, onWidthChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    val widthClass = widthClassOf(widthDp)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardColor,
        border = BorderStroke(1.dp, Line)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Window width",
                    modifier = Modifier.weight(1f),
                    color = Muted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${widthDp.roundToInt()} dp · ${widthClass.label}",
                    color = Ink,
                    fontWeight = FontWeight.Black
                )
            }
            Slider(
                value = widthDp,
                onValueChange = onWidthChange,
                valueRange = MIN_PREVIEW_WIDTH..MAX_PREVIEW_WIDTH,
                modifier = Modifier.semantics {
                    contentDescription = "Window width"
                    stateDescription = "${widthDp.roundToInt()} dp, ${widthClass.label}"
                }
            )
            Text(
                text = "${widthClass.label}: ${widthClass.range}. Bottom navigation under 600 dp, " +
                    "a rail up to 839 dp, list and detail from 840 dp.",
                color = Muted,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}


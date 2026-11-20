package com.kevin.labs.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Horizontally scrolling pill tabs. Accessible by construction: each pill is a [Role.Tab] with a
 * selected state inside a selectable group, and is at least 48 dp tall.
 *
 * The selected pill scrolls itself to the middle of the row, and the edges fade out while more
 * pills hide beyond them. Pair it with [swipeBetweenTabs] on the demo panel to change pattern
 * with a horizontal swipe.
 *
 * @param compact smaller pills (12 dp padding, 15 sp) for labs with six or more tabs.
 */
@Composable
fun <T> LabTabs(
    items: List<T>,
    label: (T) -> String,
    accent: (T) -> Color,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val scroll = rememberScrollState()
    // Start and width of each pill in the row's own (unscrolled) coordinates, in px.
    val pills = remember { mutableStateMapOf<Int, IntRange>() }
    var viewport by remember { mutableIntStateOf(0) }
    var animate by remember { mutableStateOf(false) }
    val target = pills[selectedIndex]
    LaunchedEffect(selectedIndex, viewport, target, scroll.maxValue) {
        if (target == null || viewport == 0) return@LaunchedEffect
        val to = centeredScroll(target.first, target.last - target.first, viewport, scroll.maxValue)
        // No animation on first display: the row simply opens on the selected pill.
        if (animate) scroll.animateScrollTo(to) else scroll.scrollTo(to)
        animate = true
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { viewport = it.width }
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                val fade = 32.dp.toPx()
                if (scroll.canScrollForward) {
                    drawRect(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, Color.Black),
                            startX = size.width - fade,
                            endX = size.width
                        ),
                        blendMode = BlendMode.DstOut
                    )
                }
                if (scroll.canScrollBackward) {
                    drawRect(
                        Brush.horizontalGradient(listOf(Color.Black, Color.Transparent), startX = 0f, endX = fade),
                        blendMode = BlendMode.DstOut
                    )
                }
            }
            .horizontalScroll(scroll)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEachIndexed { index, item ->
            val selected = selectedIndex == index
            val color = accent(item)
            Surface(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .onGloballyPositioned {
                        val x = it.positionInParent().x.toInt()
                        pills[index] = x..(x + it.size.width)
                    }
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelected(index) }),
                shape = RoundedCornerShape(50),
                color = if (selected) color else Color.White,
                border = BorderStroke(1.dp, if (selected) color else Line)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(
                            horizontal = if (compact) 12.dp else 16.dp,
                            vertical = if (compact) 9.dp else 10.dp
                        ),
                    text = label(item),
                    color = if (selected) Color.White else Ink,
                    fontWeight = FontWeight.Black,
                    fontSize = if (compact) 15.sp else 16.sp,
                    lineHeight = if (compact) 18.sp else 24.sp
                )
            }
        }
        // Keeps the last pill clear of the screen edge once scrolled to the end.
        Spacer(modifier = Modifier.width(10.dp))
    }
}

/** Scroll offset that puts an item starting at [itemStart] and [itemWidth] wide in the middle. */
fun centeredScroll(itemStart: Int, itemWidth: Int, viewportWidth: Int, maxScroll: Int): Int =
    (itemStart + itemWidth / 2 - viewportWidth / 2).coerceIn(0, maxOf(0, maxScroll))

/**
 * Tab a horizontal swipe of [dragPx] leads to: past [thresholdPx], a swipe towards the start
 * (content pushed left in LTR) shows the next pattern, towards the end the previous one. Stays
 * within 0 until [count].
 */
fun swipeTarget(selectedIndex: Int, count: Int, dragPx: Float, thresholdPx: Float, rtl: Boolean = false): Int {
    val forward = if (rtl) dragPx > thresholdPx else dragPx < -thresholdPx
    val backward = if (rtl) dragPx < -thresholdPx else dragPx > thresholdPx
    return when {
        forward -> (selectedIndex + 1).coerceAtMost(count - 1)
        backward -> (selectedIndex - 1).coerceAtLeast(0)
        else -> selectedIndex
    }.coerceAtLeast(0)
}

/**
 * Lets a lab's demo panel change pattern with a horizontal swipe, like turning a page. Taps and
 * vertical scrolling inside the panel keep working; panels that use horizontal gestures
 * themselves (swipe actions) should not use it.
 */
fun Modifier.swipeBetweenTabs(selectedIndex: Int, count: Int, onSelected: (Int) -> Unit): Modifier = composed {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val select by rememberUpdatedState(onSelected)
    pointerInput(selectedIndex, count, rtl) {
        val threshold = 56.dp.toPx()
        var total = 0f
        detectHorizontalDragGestures(
            onDragStart = { total = 0f },
            onDragEnd = {
                val target = swipeTarget(selectedIndex, count, total, threshold, rtl)
                if (target != selectedIndex) select(target)
            },
            onHorizontalDrag = { change, delta ->
                total += delta
                change.consume()
            }
        )
    }
}

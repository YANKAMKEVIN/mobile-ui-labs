package com.kevin.labs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** The little picture of what each lab lets you handle, drawn on the catalog card. */
enum class Vignette { Adaptive, Sheet, Empty, Feedback, Loading, Backstack, Offline, Permission, Search, Swipe }

/**
 * A 56 dp tile with a tiny drawing of the lab's subject in its [accent] colour: a sheet rising,
 * a row sliding, a skeleton... Purely decorative (the card already carries the title).
 */
@Composable
fun LabVignette(vignette: Vignette, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(10.dp)
    ) {
        Canvas(Modifier.size(36.dp)) {
            val soft = accent.copy(alpha = 0.35f)
            when (vignette) {
                Vignette.Adaptive -> adaptive(accent, soft)
                Vignette.Sheet -> sheet(accent, soft)
                Vignette.Empty -> empty(accent, soft)
                Vignette.Feedback -> feedback(accent, soft)
                Vignette.Loading -> loading(accent, soft)
                Vignette.Backstack -> backstack(accent, soft)
                Vignette.Offline -> offline(accent, soft)
                Vignette.Permission -> permission(accent, soft)
                Vignette.Search -> search(accent, soft)
                Vignette.Swipe -> swipe(accent, soft)
            }
        }
    }
}

private fun DrawScope.u(f: Float) = size.minDimension * f

private fun DrawScope.box(color: Color, x: Float, y: Float, w: Float, h: Float, r: Float = 0.06f) =
    drawRoundRect(color, Offset(u(x), u(y)), Size(u(w), u(h)), CornerRadius(u(r)))

private fun DrawScope.frame(color: Color, x: Float, y: Float, w: Float, h: Float, r: Float = 0.08f) =
    drawRoundRect(color, Offset(u(x), u(y)), Size(u(w), u(h)), CornerRadius(u(r)), style = Stroke(u(0.07f)))

private fun DrawScope.bar(color: Color, x1: Float, y: Float, x2: Float, width: Float = 0.08f) =
    drawLine(color, Offset(u(x1), u(y)), Offset(u(x2), u(y)), u(width), StrokeCap.Round)

/** A phone next to a wider tablet with a side pane. */
private fun DrawScope.adaptive(ink: Color, soft: Color) {
    frame(ink, 0.02f, 0.22f, 0.3f, 0.56f)
    frame(ink, 0.42f, 0.12f, 0.56f, 0.76f)
    box(soft, 0.5f, 0.2f, 0.14f, 0.6f)
    bar(ink, 0.72f, 0.32f, 0.88f)
    bar(soft, 0.72f, 0.48f, 0.88f)
}

/** A screen with a sheet rising from the bottom edge, handle on top. */
private fun DrawScope.sheet(ink: Color, soft: Color) {
    frame(soft, 0.14f, 0.02f, 0.72f, 0.96f, 0.12f)
    box(ink, 0.14f, 0.5f, 0.72f, 0.48f, 0.12f)
    bar(Color.White, 0.42f, 0.6f, 0.58f, 0.06f)
}

/** An empty dashed tray with a plus: nothing yet, here is how to start. */
private fun DrawScope.empty(ink: Color, soft: Color) {
    drawCircle(soft, radius = u(0.44f), center = Offset(u(0.5f), u(0.5f)), style = Stroke(u(0.07f)))
    bar(ink, 0.34f, 0.5f, 0.66f, 0.1f)
    drawLine(ink, Offset(u(0.5f), u(0.34f)), Offset(u(0.5f), u(0.66f)), u(0.1f), StrokeCap.Round)
}

/** Content lines with a snackbar sitting at the bottom. */
private fun DrawScope.feedback(ink: Color, soft: Color) {
    bar(soft, 0.08f, 0.14f, 0.92f)
    bar(soft, 0.08f, 0.32f, 0.72f)
    bar(soft, 0.08f, 0.5f, 0.82f)
    box(ink, 0.02f, 0.66f, 0.96f, 0.3f, 0.1f)
    bar(Color.White, 0.14f, 0.81f, 0.52f, 0.07f)
    bar(Color.White, 0.72f, 0.81f, 0.84f, 0.07f)
}

/** A skeleton: avatar placeholder and grey lines, one of them filling up. */
private fun DrawScope.loading(ink: Color, soft: Color) {
    drawCircle(soft, radius = u(0.14f), center = Offset(u(0.16f), u(0.2f)))
    bar(soft, 0.42f, 0.13f, 0.94f)
    bar(soft, 0.42f, 0.3f, 0.76f)
    bar(soft, 0.04f, 0.58f, 0.96f)
    bar(soft, 0.04f, 0.8f, 0.96f)
    bar(ink, 0.04f, 0.8f, 0.58f)
}

/** A stack of screens, the top one in front. */
private fun DrawScope.backstack(ink: Color, soft: Color) {
    box(soft, 0.36f, 0.02f, 0.6f, 0.64f, 0.1f)
    box(soft, 0.2f, 0.16f, 0.6f, 0.64f, 0.1f)
    box(ink, 0.04f, 0.32f, 0.6f, 0.64f, 0.1f)
    bar(Color.White, 0.14f, 0.5f, 0.42f, 0.07f)
}

/** A note with a queue of three pending dots beside it, the first one synced. */
private fun DrawScope.offline(ink: Color, soft: Color) {
    frame(ink, 0.04f, 0.06f, 0.6f, 0.88f, 0.1f)
    bar(soft, 0.16f, 0.28f, 0.52f)
    bar(soft, 0.16f, 0.46f, 0.52f)
    bar(soft, 0.16f, 0.64f, 0.4f)
    drawCircle(ink, radius = u(0.08f), center = Offset(u(0.86f), u(0.22f)))
    drawCircle(soft, radius = u(0.08f), center = Offset(u(0.86f), u(0.5f)))
    drawCircle(soft, radius = u(0.08f), center = Offset(u(0.86f), u(0.78f)))
}

/** A small prompt card with a quiet and a primary button. */
private fun DrawScope.permission(ink: Color, soft: Color) {
    frame(ink, 0.04f, 0.12f, 0.92f, 0.76f, 0.12f)
    bar(soft, 0.18f, 0.32f, 0.82f)
    bar(soft, 0.18f, 0.48f, 0.62f)
    box(soft, 0.16f, 0.62f, 0.3f, 0.14f)
    box(ink, 0.54f, 0.62f, 0.3f, 0.14f)
}

/** A magnifier over result lines. */
private fun DrawScope.search(ink: Color, soft: Color) {
    bar(soft, 0.04f, 0.74f, 0.96f)
    bar(soft, 0.04f, 0.92f, 0.7f)
    drawCircle(ink, radius = u(0.22f), center = Offset(u(0.4f), u(0.3f)), style = Stroke(u(0.09f)))
    drawLine(ink, Offset(u(0.56f), u(0.46f)), Offset(u(0.74f), u(0.62f)), u(0.1f), StrokeCap.Round)
}

/** A row slid to the left, revealing its action behind. */
private fun DrawScope.swipe(ink: Color, soft: Color) {
    box(ink, 0.02f, 0.28f, 0.96f, 0.44f, 0.12f)
    drawCircle(Color.White, radius = u(0.08f), center = Offset(u(0.84f), u(0.5f)))
    box(soft.compositeOver(Color.White), 0.02f, 0.28f, 0.64f, 0.44f, 0.12f)
    bar(ink, 0.06f, 0.43f, 0.42f, 0.07f)
    bar(ink, 0.06f, 0.57f, 0.28f, 0.07f)
}

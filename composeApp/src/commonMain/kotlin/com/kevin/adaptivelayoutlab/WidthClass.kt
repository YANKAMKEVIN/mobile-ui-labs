package com.kevin.adaptivelayoutlab

/**
 * Material window width classes, the thresholds at which the lab's layout changes hierarchy:
 * bottom navigation below 600 dp, a rail up to 840 dp, list + detail beyond.
 */
internal enum class WidthClass(val label: String, val range: String) {
    Compact("Compact", "under 600 dp"),
    Medium("Medium", "600 to 839 dp"),
    Expanded("Expanded", "840 dp and up")
}

internal const val MIN_PREVIEW_WIDTH = 320f
internal const val MAX_PREVIEW_WIDTH = 1200f

internal fun widthClassOf(widthDp: Float): WidthClass = when {
    widthDp < 600f -> WidthClass.Compact
    widthDp < 840f -> WidthClass.Medium
    else -> WidthClass.Expanded
}

/** A typical window width for each class, used when a tab is picked by hand. */
internal fun typicalWidth(widthClass: WidthClass): Float = when (widthClass) {
    WidthClass.Compact -> 400f
    WidthClass.Medium -> 720f
    WidthClass.Expanded -> 1000f
}

/**
 * Share of the preview card the mock window takes for [widthDp]: the narrowest window still
 * fills half the card, the widest all of it.
 */
internal fun previewFraction(widthDp: Float): Float {
    val t = (widthDp - MIN_PREVIEW_WIDTH) / (MAX_PREVIEW_WIDTH - MIN_PREVIEW_WIDTH)
    return 0.5f + 0.5f * t.coerceIn(0f, 1f)
}

/**
 * Width given to the simulated window when it follows the real one: the real width, kept inside
 * the slider's range (a phone narrower than 320 dp shows as 320, a desktop wider than 1200 as 1200).
 * The width class shown next to the switch is the real one, unclamped.
 */
internal fun followedWidth(realWidthDp: Float): Float = realWidthDp.coerceIn(MIN_PREVIEW_WIDTH, MAX_PREVIEW_WIDTH)

package com.kevin.labs.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class LabTabsTest {
    @Test
    fun selectedPillIsCentredInTheViewport() {
        // Pill from 500 to 600 px in a 400 px viewport: its middle (550) lands at 200.
        assertEquals(350, centeredScroll(itemStart = 500, itemWidth = 100, viewportWidth = 400, maxScroll = 1000))
    }

    @Test
    fun centringNeverScrollsPastTheEnds() {
        assertEquals(0, centeredScroll(itemStart = 0, itemWidth = 100, viewportWidth = 400, maxScroll = 1000))
        assertEquals(300, centeredScroll(itemStart = 900, itemWidth = 100, viewportWidth = 400, maxScroll = 300))
        assertEquals(0, centeredScroll(itemStart = 200, itemWidth = 100, viewportWidth = 400, maxScroll = 0))
    }

    @Test
    fun swipeTowardsTheStartShowsTheNextPattern() {
        assertEquals(3, swipeTarget(selectedIndex = 2, count = 5, dragPx = -120f, thresholdPx = 100f))
        assertEquals(1, swipeTarget(selectedIndex = 2, count = 5, dragPx = 120f, thresholdPx = 100f))
    }

    @Test
    fun shortSwipesAndEdgesKeepTheCurrentPattern() {
        assertEquals(2, swipeTarget(selectedIndex = 2, count = 5, dragPx = -80f, thresholdPx = 100f))
        assertEquals(4, swipeTarget(selectedIndex = 4, count = 5, dragPx = -300f, thresholdPx = 100f))
        assertEquals(0, swipeTarget(selectedIndex = 0, count = 5, dragPx = 300f, thresholdPx = 100f))
    }

    @Test
    fun rightToLeftLayoutsMirrorTheGesture() {
        assertEquals(3, swipeTarget(selectedIndex = 2, count = 5, dragPx = 120f, thresholdPx = 100f, rtl = true))
        assertEquals(1, swipeTarget(selectedIndex = 2, count = 5, dragPx = -120f, thresholdPx = 100f, rtl = true))
    }
}

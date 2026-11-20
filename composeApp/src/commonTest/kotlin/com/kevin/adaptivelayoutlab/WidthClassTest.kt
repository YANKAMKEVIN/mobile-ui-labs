package com.kevin.adaptivelayoutlab

import kotlin.test.Test
import kotlin.test.assertEquals

class WidthClassTest {
    @Test
    fun thresholdsFollowMaterialWidthClasses() {
        assertEquals(WidthClass.Compact, widthClassOf(320f))
        assertEquals(WidthClass.Compact, widthClassOf(599.9f))
        assertEquals(WidthClass.Medium, widthClassOf(600f))
        assertEquals(WidthClass.Medium, widthClassOf(839f))
        assertEquals(WidthClass.Expanded, widthClassOf(840f))
    }

    @Test
    fun typicalWidthFallsInItsOwnClass() {
        WidthClass.entries.forEach { assertEquals(it, widthClassOf(typicalWidth(it))) }
    }

    @Test
    fun previewNarrowsWithTheWindowButStaysReadable() {
        assertEquals(0.5f, previewFraction(MIN_PREVIEW_WIDTH))
        assertEquals(1f, previewFraction(MAX_PREVIEW_WIDTH))
        assertEquals(0.5f, previewFraction(100f))
        assertEquals(0.75f, previewFraction((MIN_PREVIEW_WIDTH + MAX_PREVIEW_WIDTH) / 2))
    }
}

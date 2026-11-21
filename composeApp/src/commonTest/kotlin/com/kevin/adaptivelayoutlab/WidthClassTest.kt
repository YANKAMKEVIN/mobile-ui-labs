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

    @Test
    fun followingTheWindowKeepsTheRealWidthInsideTheSlider() {
        assertEquals(412f, followedWidth(412f))
        assertEquals(MIN_PREVIEW_WIDTH, followedWidth(280f))
        assertEquals(MAX_PREVIEW_WIDTH, followedWidth(1920f))
        // A phone in portrait, a small tablet and a desktop window land in the three classes.
        assertEquals(WidthClass.Compact, widthClassOf(followedWidth(412f)))
        assertEquals(WidthClass.Medium, widthClassOf(followedWidth(673f)))
        assertEquals(WidthClass.Expanded, widthClassOf(followedWidth(1920f)))
    }
}

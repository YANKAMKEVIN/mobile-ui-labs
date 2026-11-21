package com.kevin.labs.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccentTextTest {
    // A sample of the labs' accents, from the darkest (Slate) to the lightest (Gold).
    private val accents = listOf(
        Color(0xFF34444B), Color(0xFF0D6B57), Color(0xFF2563A8), Color(0xFF6D56B3),
        Color(0xFFC8523A), Color(0xFFD23B3B), Color(0xFFB76F16)
    )

    @Test
    fun contrastRatioMatchesWcagReferences() {
        assertEquals(21f, contrastRatio(Color.Black, Color.White), 0.01f)
        assertEquals(1f, contrastRatio(Color(0xFF2563A8), Color(0xFF2563A8)), 0.001f)
        // Symmetric.
        assertEquals(contrastRatio(Color.Black, Color.Gray), contrastRatio(Color.Gray, Color.Black), 0.001f)
    }

    @Test
    fun everyAccentIsReadableOnTheNightCardsAndPaper() {
        accents.forEach { accent ->
            val text = readableOn(accent, DarkLabPalette.card)
            assertTrue(contrastRatio(text, DarkLabPalette.card) >= TEXT_CONTRAST, "$accent on card")
            assertTrue(contrastRatio(text, DarkLabPalette.paper) >= TEXT_CONTRAST, "$accent on paper")
        }
    }

    @Test
    fun anAccentThatIsAlreadyReadableIsKept() {
        accents.forEach { assertEquals(it, readableOn(it, LightLabPalette.card, 3.9f)) }
    }

    @Test
    fun lighteningKeepsTheHueFamily() {
        // The night version of the blue accent is still blue first, not white or grey.
        val blue = readableOn(Color(0xFF2563A8), DarkLabPalette.card)
        assertTrue(blue.blue > blue.red && blue.blue > blue.green, "$blue")
        assertTrue(blue != Color.White)
    }

    @Test
    fun aLightBackgroundDarkensInstead() {
        val text = readableOn(Color(0xFFFFD166), Color.White)
        assertTrue(contrastRatio(text, Color.White) >= TEXT_CONTRAST)
        assertTrue(text.luminance() < Color(0xFFFFD166).luminance())
    }
}

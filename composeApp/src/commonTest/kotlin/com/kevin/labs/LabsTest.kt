package com.kevin.labs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LabsTest {
    @Test
    fun catalogComesBeforeEveryLab() {
        assertEquals(-1, labOrder(null))
        labs.forEachIndexed { index, lab -> assertEquals(index, labOrder(lab.id)) }
    }

    @Test
    fun labIdsAreUnique() {
        assertTrue(labs.map { it.id }.toSet().size == labs.size)
    }
}

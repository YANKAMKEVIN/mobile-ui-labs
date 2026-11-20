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

    @Test
    fun neighboursFollowTheCatalogOrder() {
        val (previous, next) = neighbours(labs[3].id)
        assertEquals(labs[2], previous)
        assertEquals(labs[4], next)
    }

    @Test
    fun firstAndLastLabsHaveOneNeighbour() {
        assertEquals(null to labs[1], neighbours(labs.first().id))
        assertEquals(labs[labs.size - 2] to null, neighbours(labs.last().id))
        assertEquals(null to null, neighbours("unknown"))
    }

    @Test
    fun catalogTurnsIntoAGridOnWideWindows() {
        assertEquals(1, catalogColumns(412f))
        assertEquals(2, catalogColumns(600f))
        assertEquals(2, catalogColumns(839f))
        assertEquals(3, catalogColumns(1280f))
    }

    @Test
    fun groupsAreContiguousSoNumbersReadInOrder() {
        assertEquals(labs, labsByGroup().flatMap { it.second })
        assertTrue(labsByGroup().all { it.second.isNotEmpty() })
    }

    @Test
    fun labsAreNumberedFromOneToTen() {
        assertEquals("01", labs.first().number())
        assertEquals("10", labs.last().number())
        assertEquals((1..labs.size).map { it.toString().padStart(2, '0') }, labs.map { it.number() })
    }
}

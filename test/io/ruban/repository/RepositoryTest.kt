package io.ruban.repository

import io.ruban.entity.Instrument
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class RepositoryTest {

    private lateinit var instrumentsTable: HashMap<String, Instrument>
    private lateinit var quotesTable: HashMap<String, SortedMap<OffsetDateTime, Double>>

    private lateinit var unit: Repository


    @BeforeTest
    fun beforeEach() {
        instrumentsTable = HashMap()
        quotesTable = HashMap()
        unit = Repository(instruments = instrumentsTable, quotes = quotesTable)
    }

    @Test
    fun `should list all active instruments`() {
        instrumentsTable["isin-1"] = Instrument(isin = "isin-1", description = "isin-1-active")
        instrumentsTable["isin-2"] = Instrument(isin = "isin-2", description = "isin-2-active")
        instrumentsTable["isin-3"] = Instrument(isin = "isin-3", description = "isin-3-disabled", isActive = false)

        val list = unit.activeInstruments()
        assertEquals(2, list.size)
        assertTrue(instrumentsTable.containsKey("isin-1"))
        assertTrue(instrumentsTable.containsKey("isin-2"))
    }

    @Test
    fun `should add instruments`() {
        assertEquals(0, instrumentsTable.size)
        unit.activate(isin = "isin-1", description = "isin-1-active")
        unit.activate(isin = "isin-2", description = "isin-2-active")
        assertEquals(2, instrumentsTable.size)
    }

    @Test
    fun `should not duplicate instruments`() {
        assertEquals(0, instrumentsTable.size)
        unit.activate(isin = "isin-1", description = "isin-1-first")
        unit.activate(isin = "isin-1", description = "isin-1-second")
        assertEquals(1, instrumentsTable.size)

        assertTrue(instrumentsTable["isin-1"]!!.isActive)
        assertEquals("isin-1-second", instrumentsTable["isin-1"]!!.description)
    }

    @Test
    fun `should disable instruments`() {
        instrumentsTable["isin-1"] = Instrument(isin = "isin-1", description = "isin-1-active")
        instrumentsTable["isin-2"] = Instrument(isin = "isin-2", description = "isin-2-active")

        unit.disable(isin = "isin-1", description = "isin-1-disabled")
        unit.disable(isin = "isin-2", description = "isin-2-disabled")

        assertEquals(2, instrumentsTable.size)
        assertFalse(instrumentsTable["isin-1"]!!.isActive)
        assertFalse(instrumentsTable["isin-1"]!!.isActive)
        assertEquals("isin-1-disabled", instrumentsTable["isin-1"]!!.description)
        assertEquals("isin-2-disabled", instrumentsTable["isin-2"]!!.description)
    }

    @Test
    fun `should quote instrument`() {
        instrumentsTable["isin-1"] = Instrument(isin = "isin-1", description = "isin-1-active", price = null)

        assertTrue(quotesTable.isEmpty())

        unit.quote("isin-1", 3.0, OffsetDateTime.now().minusMinutes(5))
        unit.quote("isin-1", 3.5, OffsetDateTime.now().minusMinutes(4))
        unit.quote("isin-1", 4.0, OffsetDateTime.now().minusMinutes(3))

        assertEquals(3, quotesTable["isin-1"]!!.size)
        assertEquals(4.0, instrumentsTable["isin-1"]!!.price)
    }

    @Test
    fun `should get quotes for last 30 minutes`() {
        unit.quote("isin-1", 3.0, OffsetDateTime.now().minusMinutes(60))
        unit.quote("isin-1", 3.5, OffsetDateTime.now().minusMinutes(5))
        unit.quote("isin-1", 4.6, OffsetDateTime.now().minusMinutes(3))
        unit.quote("isin-1", 5.4, OffsetDateTime.now().minusMinutes(2))
        unit.quote("isin-1", 3.2)

        val list = unit.lastQuotes(isin = "isin-1", minutes = 30)
        assertEquals(5, quotesTable["isin-1"]!!.size)
        assertEquals(4, list.size)
    }
}
package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.repository.Repository
import org.junit.Assert.assertEquals
import java.time.OffsetDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test

class DataAggregatorTest {

    private lateinit var flipFlops: MutableMap<String, MutableMap<OffsetDateTime, Boolean>>
    private lateinit var repository: Repository
    private lateinit var unit: DataAggregator

    @BeforeTest
    fun `before each`() {
        flipFlops = mutableMapOf()
        repository = Repository()
        unit = DataAggregator(repository, flipFlops)
    }

    @Test
    fun `should list all active instruments`() {
        (1..100).forEach { repository.activate(isin = "isin-$it", description = "isin-$it-active") }

        assertEquals(100, unit.list().size)

        (1..30).forEach { repository.disable(isin = "isin-$it", description = "isin-$it-active") }

        assertEquals(70, unit.list().size)
    }

    @Test
    fun `should generate a candlestick for given ISIN`() {
        val openTimestamp = OffsetDateTime.now()
        val openPrice = 40.0
        val highPrice = 80.0
        val lowPrice = 20.0
        val closingPrice = 60.0

        (0 until 10).forEach {
            when (it) {
                0 -> {
                    repository.quote(
                        isin = "isin",
                        price = openPrice,
                        timestamp = openTimestamp.plusSeconds(it.toLong())
                    )
                }
                4 -> {
                    repository.quote(
                        isin = "isin",
                        price = highPrice,
                        timestamp = openTimestamp.plusSeconds(it.toLong())
                    )
                }
                5 -> {
                    repository.quote(
                        isin = "isin",
                        price = lowPrice,
                        timestamp = openTimestamp.plusSeconds(it.toLong())
                    )
                }
                9 -> {
                    repository.quote(
                        isin = "isin",
                        price = closingPrice,
                        timestamp = openTimestamp.plusSeconds(it.toLong())
                    )
                }
                else -> {
                    repository.quote(
                        isin = "isin",
                        price = openPrice + it,
                        timestamp = openTimestamp.plusSeconds(it.toLong())
                    )
                }
            }
        }

        val candles = unit.candles(isin = "isin", period = 30)
        assertEquals(1, candles.size)
        assertEquals(
            Candlestick(
                openTime = openTimestamp,
                openPrice = openPrice,
                closingPrice = closingPrice,
                highPrice = highPrice,
                lowPrice = lowPrice,
                closingTime = openTimestamp.plusSeconds(9)
            ), candles[0]
        )
    }

    @Test
    fun `should register a rapidly going up instrument`() {
        repository.activate(isin = "isin", description = "isin-active")
        repository.quote(isin = "isin", price = 10.0, timestamp = OffsetDateTime.now().minusMinutes(3))
        repository.quote(isin = "isin", price = 12.0, timestamp = OffsetDateTime.now())

        unit.analyzeFlipFlops(period = 5)

        assertEquals(1, flipFlops.size)
    }

    @Test
    fun `should register a going up flip-flop`() {
        repository.activate(isin = "isin", description = "isin-active")

        val wasGoingUp = true
        flipFlops["isin"] = mutableMapOf(Pair(OffsetDateTime.now().minusMinutes(4), wasGoingUp))

        repository.quote(isin = "isin", price = 11.0, timestamp = OffsetDateTime.now().minusMinutes(3))
        repository.quote(isin = "isin", price = 9.0, timestamp = OffsetDateTime.now())

        unit.analyzeFlipFlops(period = 5)

        assertEquals(2, flipFlops["isin"]!!.size)
    }

    @Test
    fun `should register a rapidly going down instrument`() {
        repository.activate(isin = "isin", description = "isin-active")
        repository.quote(isin = "isin", price = 10.0, timestamp = OffsetDateTime.now().minusMinutes(3))
        repository.quote(isin = "isin", price = 8.9, timestamp = OffsetDateTime.now())

        unit.analyzeFlipFlops(period = 5)

        assertEquals(1, flipFlops.size)
    }

    @Test
    fun `should register a going down flip-flop`() {
        repository.activate(isin = "isin", description = "isin-active")

        val wasGoingUp = false
        flipFlops["isin"] = mutableMapOf(Pair(OffsetDateTime.now().minusMinutes(4), wasGoingUp))

        repository.quote(isin = "isin", price = 11.0, timestamp = OffsetDateTime.now().minusMinutes(3))
        repository.quote(isin = "isin", price = 9.0, timestamp = OffsetDateTime.now())

        unit.analyzeFlipFlops(period = 5)

        assertEquals(2, flipFlops["isin"]!!.size)
    }
}
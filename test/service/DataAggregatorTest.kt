package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.repository.Repository
import org.junit.Assert.assertEquals
import java.time.OffsetDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test

class DataAggregatorTest {

    private lateinit var repository: Repository
    private lateinit var unit: DataAggregator

    @BeforeTest
    fun `before each`() {
        repository = Repository()
        unit = DataAggregator(repository)
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
}
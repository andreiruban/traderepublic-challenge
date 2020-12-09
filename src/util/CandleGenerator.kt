package io.ruban.util

import io.ruban.entity.Candlestick
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun generate(quotes: SortedMap<OffsetDateTime, Double>): Map<Int, Candlestick> {
    val candles: MutableMap<Int, Candlestick> = sortedMapOf()

    val chunks = quotes.keys.groupBy { time -> time.minute }.toSortedMap()
    val index = AtomicInteger(0)
    try {
        chunks.forEach { (_, timeChunk) ->

            val openTime = timeChunk.minOrNull()!!
            val closingTime = timeChunk.maxOrNull()!!
            val highPrice = quotes.filterKeys { time -> time in timeChunk }.values.maxOrNull()!!
            val lowPrice = quotes.filterKeys { time -> time in timeChunk }.values.minOrNull()!!

            candles[index.getAndIncrement()] = Candlestick(
                openTime = openTime,
                closingTime = closingTime,
                openPrice = quotes[openTime]!!,
                closingPrice = quotes[closingTime]!!,
                highPrice = highPrice,
                lowPrice = lowPrice
            )
        }

    } catch (npe: NullPointerException) {
        throw RuntimeException("Inconsistent Data")
    }
    return candles
}

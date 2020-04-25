package io.ruban.util

import io.ruban.entity.Candlestick
import java.time.OffsetDateTime
import java.util.*

fun toCandles(quotes: SortedMap<OffsetDateTime, Double>): List<Candlestick> {
    val candles: MutableList<Candlestick> = mutableListOf()

    val chunks = quotes.keys.groupBy { time -> time.minute }.toSortedMap()
    try {
        chunks.forEach { (_, timeChunk) ->

            val openTime = timeChunk.min()!!
            val closingTime = timeChunk.max()!!
            val highPrice = quotes.filterKeys { time -> time in timeChunk }.values.max()!!
            val lowPrice = quotes.filterKeys { time -> time in timeChunk }.values.min()!!

            candles.add(
                Candlestick(
                    openTime = openTime,
                    closingTime = closingTime,
                    openPrice = quotes[openTime]!!,
                    closingPrice = quotes[closingTime]!!,
                    highPrice = highPrice,
                    lowPrice = lowPrice
                )
            )
        }

    } catch (npe: NullPointerException) {
        throw RuntimeException("Inconsistent Data")
    }
    return candles
}

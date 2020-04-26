package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.entity.Instrument
import io.ruban.repository.Repository
import io.ruban.util.generate
import io.ruban.util.validateISIN
import io.ruban.util.validatePeriod
import io.ruban.util.view
import java.time.OffsetDateTime

class DataAggregator(
    private val repository: Repository,
    private val flipFlops: MutableMap<String, MutableMap<OffsetDateTime, Boolean>> = mutableMapOf(),
    private val pullState: MutableMap<OffsetDateTime, String> = mutableMapOf()
) {

    fun list() = repository.activeInstruments().map(Instrument::view)

    fun candles(isin: String, period: Long): Map<Int, Candlestick> {
        validateISIN(isin)
        validatePeriod(period)
        return generate(repository.lastQuotes(isin = isin, minutes = period))
    }

    fun getHotOffer(): Pair<String, Boolean>? {
        val ffMapToOffer = flipFlops.filterValues { map -> map.keys.last() !in pullState.keys }
        val isin = ffMapToOffer.keys.firstOrNull()
        return if (isin != null) {
            val time = ffMapToOffer.getValue(isin).keys.first()
            val state = ffMapToOffer.getValue(isin).values.first()
            pullState[time] = isin
            Pair(isin, state)
        } else null

    }

    // primitive
    fun analyzeFlipFlops(period: Long) {
        repository.activeInstruments()
            .map(Instrument::isin)
            .forEach { isin ->
                val candles = generate(repository.lastQuotes(isin = isin, minutes = period))
                val aPrice = candles.values.first().closingPrice
                val bPrice = candles.values.last().closingPrice

                val isGoingUp = bPrice > aPrice
                if (isGoingUp) {
                    if (bPrice.minus(aPrice).div(bPrice) * 100 > 10) {
                        if (isin in flipFlops.keys && !flipFlops[isin]!!.values.last()) {
                            flipFlops[isin]!![OffsetDateTime.now()] = isGoingUp
                        } else {
                            flipFlops[isin] = mutableMapOf(Pair(OffsetDateTime.now(), isGoingUp))
                        }
                    }
                } else {
                    if ((aPrice.minus(bPrice).div(aPrice) * 100) > 10) {
                        if (isin in flipFlops.keys && !flipFlops[isin]!!.values.last()) {
                            flipFlops[isin]!![OffsetDateTime.now()] = isGoingUp
                        } else {
                            flipFlops[isin] = mutableMapOf(Pair(OffsetDateTime.now(), isGoingUp))
                        }
                    }
                }
            }
    }
}

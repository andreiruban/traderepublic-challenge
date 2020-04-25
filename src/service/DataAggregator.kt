package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.entity.CandlestickView
import io.ruban.entity.Instrument
import io.ruban.repository.Repository
import io.ruban.util.toCandles
import io.ruban.util.validateISIN
import io.ruban.util.validatePeriod
import io.ruban.util.view

class DataAggregator(
    private val repository: Repository
) {
    fun list() = repository.activeInstruments().map(Instrument::view)

    fun candles(isin: String, period: Long): List<CandlestickView> {
        validateISIN(isin)
        validatePeriod(period)
        return toCandles(repository.lastQuotes(isin = isin, minutes = period)).map(Candlestick::view)
    }
}

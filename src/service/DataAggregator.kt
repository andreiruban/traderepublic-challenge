package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.entity.Instrument
import io.ruban.repository.Repository
import io.ruban.util.toCandles
import io.ruban.util.view
import java.util.*

class DataAggregator(
    private val repository: Repository
) {
    fun list() = repository.activeInstruments().map(Instrument::view)

    fun candles(isin: String, period: Long): SortedMap<Int, Candlestick> {


        return toCandles(repository.lastQuotes(isin = isin, minutes = period))
    }

}


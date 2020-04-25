package io.ruban.service

import io.ruban.entity.Candlestick
import io.ruban.entity.Instrument
import io.ruban.repository.Repository
import io.ruban.util.view

class DataAggregator(
    private val repository: Repository
) {
    fun list() = repository.allActive().map(Instrument::view)

    fun candles(minutes: Int): Collection<Candlestick> {

        return emptyList()
    }
}
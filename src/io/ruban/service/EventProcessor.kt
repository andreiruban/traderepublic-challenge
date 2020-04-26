package io.ruban.service

import io.ruban.entity.Event
import io.ruban.entity.EventType.*
import io.ruban.entity.InstrumentEvent
import io.ruban.entity.QuoteEvent
import io.ruban.repository.Repository
import io.ruban.util.validateISIN
import io.ruban.util.validatePrice


class EventProcessor(
    private val repository: Repository
) {

    fun consume(event: Event) {
        validateISIN(event.data.isin)
        when (event.type) {
            ADD -> {
                val data = (event as InstrumentEvent).data
                repository.activate(isin = data.isin, description = data.description)
            }
            DELETE -> {
                val data = (event as InstrumentEvent).data
                repository.disable(isin = data.isin, description = data.description)
            }
            QUOTE -> {
                val data = (event as QuoteEvent).data
                validatePrice(data.price)
                repository.quote(isin = data.isin, price = data.price)
            }
        }
    }
}
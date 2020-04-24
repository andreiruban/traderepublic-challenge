package io.ruban.service

import io.ruban.entity.EventType.*
import io.ruban.entity.InstrumentEvent
import io.ruban.entity.QuoteEvent
import io.ruban.repository.Repository
import io.ruban.util.validateISIN
import io.ruban.util.validatePrice


class EventProcessor(
    private val repository: Repository
) {

    fun consume(event: InstrumentEvent) {
        validateISIN(event.data.isin)
        when (event.type) {
            ADD -> {
                repository.activate(isin = event.data.isin, description = event.data.description)
            }
            DELETE -> {
                repository.disable(isin = event.data.isin, description = event.data.description)
            }
            // TODO: throw custom exception here
            else -> throw RuntimeException("event type ${event.type} is unsupported for instrument processing")
        }
    }

    fun consume(event: QuoteEvent) {
        validateISIN(event.data.isin)
        validatePrice(event.data.price)
        when (event.type) {
            QUOTE -> {
                repository.update(isin = event.data.isin, price = event.data.price)
            }
            // TODO: throw custom exception here
            else -> throw RuntimeException("event type ${event.type} is unsupported for quotes processing")
        }
    }
}
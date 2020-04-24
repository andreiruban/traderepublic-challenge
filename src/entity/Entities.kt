package io.ruban.entity

import java.time.OffsetDateTime

data class InstrumentEvent(
    val data: InstrumentEventBody,
    val type: EventType
)

data class QuoteEvent(
    val data: QuoteEventBody,
    val type: EventType
)

data class InstrumentEventBody(
    val description: String,
    val isin: String
)

data class QuoteEventBody(
    val price: Double,
    val isin: String
)

data class InstrumentView(
    val isin: String,
    val description: String,
    val price: Double? = null,
    val createdAt: String
)

data class Instrument(
    val isin: String,
    val description: String,
    val price: Double? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val isDeleted: Boolean = false
)

enum class EventType {
    ADD, DELETE, QUOTE
}
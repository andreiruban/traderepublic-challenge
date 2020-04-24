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
    val createdAt: String,
    val updatedAt: String?
)

data class Instrument(
    val isin: String,
    val description: String,
    val price: Double? = null,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val updatedAt: OffsetDateTime? = null,
    val isActive: Boolean = true
)

data class Candlestick(
    val openTime: OffsetDateTime,
    val closingTime: OffsetDateTime,
    val openPrice: Double,
    val closingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double
)

enum class EventType {
    ADD, DELETE, QUOTE
}
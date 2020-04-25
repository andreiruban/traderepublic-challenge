package io.ruban.entity

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class InstrumentEvent(
    override val data: InstrumentData,
    override val type: EventType
) : Event()

data class QuoteEvent(
    override val data: QuoteData,
    override val type: EventType
) : Event()

abstract class Event {
    abstract val type: EventType
    abstract val data: Data
}

data class InstrumentData(
    val description: String,
    override val isin: String
) : Data()

data class QuoteData(
    val price: Double,
    override val isin: String
) : Data()


abstract class Data {
    abstract val isin: String
}

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

enum class EventType {
    ADD, DELETE, QUOTE
}

data class Candlestick(
    val openTime: OffsetDateTime,
    val closingTime: OffsetDateTime,
    val openPrice: Double,
    val closingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double
)

data class CandlestickView(
    val openTime: String,
    val closingTime: String,
    val openPrice: Double,
    val closingPrice: Double,
    val highPrice: Double,
    val lowPrice: Double
)

data class ResponseContainer(
    val body: Any,
    val timestamp: String = OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT)
)
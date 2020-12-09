package io.ruban.util

import io.ruban.entity.Candlestick
import io.ruban.entity.CandlestickView
import io.ruban.entity.Instrument
import io.ruban.entity.InstrumentView
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun Instrument.view(): InstrumentView = InstrumentView(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt.format(DateTimeFormatter.ISO_INSTANT),
    updatedAt = updatedAt?.format(DateTimeFormatter.ISO_INSTANT)
)

fun Instrument.active(description: String): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now(),
    isActive = true
)

fun Instrument.disabled(description: String): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now(),
    isActive = false
)

fun Instrument.quoted(price: Double): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)

fun Candlestick.view(): CandlestickView = CandlestickView(
    openTime = openTime.format(DateTimeFormatter.ISO_INSTANT),
    closingTime = closingTime.format(DateTimeFormatter.ISO_INSTANT),
    openPrice = openPrice,
    closingPrice = closingPrice,
    highPrice = highPrice,
    lowPrice = lowPrice
)

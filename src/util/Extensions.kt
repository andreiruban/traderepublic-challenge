package io.ruban.util

import io.ruban.entity.Instrument
import io.ruban.entity.InstrumentView
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Suppress("NOTHING_TO_INLINE")
inline fun Instrument.view(): InstrumentView = InstrumentView(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt.format(DateTimeFormatter.ISO_INSTANT),
    updatedAt = updatedAt?.format(DateTimeFormatter.ISO_INSTANT)
)

@Suppress("NOTHING_TO_INLINE")
inline fun Instrument.active(description: String): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now(),
    isActive = true
)

@Suppress("NOTHING_TO_INLINE")
inline fun Instrument.disabled(description: String): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now(),
    isActive = false
)

@Suppress("NOTHING_TO_INLINE")
inline fun Instrument.quoted(price: Double): Instrument = Instrument(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt,
    updatedAt = OffsetDateTime.now()
)
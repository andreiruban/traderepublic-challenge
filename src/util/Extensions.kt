package io.ruban.util

import io.ruban.entity.Instrument
import io.ruban.entity.InstrumentView
import java.time.format.DateTimeFormatter

@Suppress("NOTHING_TO_INLINE")
inline fun Instrument.view(): InstrumentView = InstrumentView(
    isin = isin,
    description = description,
    price = price,
    createdAt = createdAt.format(DateTimeFormatter.ISO_INSTANT)
)
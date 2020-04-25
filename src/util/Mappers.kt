package io.ruban.util

import io.ruban.entity.Candlestick
import io.ruban.entity.CandlestickView

fun toView(entities: Map<Int, Candlestick>): Map<Int, CandlestickView> =
    entities.mapValues { it.value.view() }
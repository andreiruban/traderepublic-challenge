package io.ruban.repository

import io.ruban.entity.Instrument
import io.ruban.util.active
import io.ruban.util.disabled
import io.ruban.util.quoted
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.HashMap

class Repository {

    private val log = LoggerFactory.getLogger(Repository::class.java)

    private val quotes: HashMap<String, SortedMap<OffsetDateTime, Double>> = HashMap()
    private val instruments: HashMap<String, Instrument> = HashMap()

    fun activeInstruments(): List<Instrument> {
        return instruments.values.filter { it.isActive }
    }

    fun lastQuotes(isin: String, minutes: Long): SortedMap<OffsetDateTime, Double> = when {
        quotes.containsKey(isin) -> {
            quotes[isin]!!.filterKeys { time -> time.isAfter(OffsetDateTime.now().minusMinutes(minutes)) }.toSortedMap()
        }
        else -> {
            log.warn("Quotes for [$isin] not found")
            sortedMapOf()
        }
    }

    fun activate(isin: String, description: String) {
        if (isin in instruments) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.active(description)
        } else instruments[isin] = Instrument(isin = isin, description = description)
    }

    fun disable(isin: String, description: String) {
        if (isin in instruments) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.disabled(description)
        } else log.warn("Instrument [$isin] not found")
    }

    fun quote(isin: String, price: Double) {
        if (isin in quotes) {
            quotes.getValue(isin).plus(Pair(OffsetDateTime.now(), price))
        } else {
            quotes[isin] = sortedMapOf(Pair(OffsetDateTime.now(), price))
        }

        if (isin in instruments) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.quoted(price)
        }
    }
}
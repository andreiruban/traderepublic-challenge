package io.ruban.repository

import io.ruban.entity.Instrument
import io.ruban.util.active
import io.ruban.util.disabled
import io.ruban.util.quoted
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

class Repository {

    private val log = LoggerFactory.getLogger(Repository::class.java)

    private val quotes: HashMap<String, MutableList<Pair<OffsetDateTime, Double>>> = HashMap()
    private val instruments: HashMap<String, Instrument> = HashMap()

    fun activeInstruments(): List<Instrument> {
        return instruments.values.filter { it.isActive }
    }

    fun activate(isin: String, description: String) {
        if (instruments.containsKey(isin)) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.active(description)
        } else instruments[isin] = Instrument(isin = isin, description = description)
    }

    fun disable(isin: String, description: String) {
        if (instruments.containsKey(isin)) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.disabled(description)
        } else log.warn("Instrument [$isin] not found")
    }

    fun quote(isin: String, price: Double) {
        if (quotes.containsKey(isin)) {
            quotes.getValue(isin).plusElement(Pair(OffsetDateTime.now(), price))
        } else {
            quotes[isin] = mutableListOf(Pair(OffsetDateTime.now(), price))
        }

        if (instruments.containsKey(isin)) {
            val entity = instruments[isin]!!
            instruments[isin] = entity.quoted(price)
        }
    }
}
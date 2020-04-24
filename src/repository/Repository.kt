package io.ruban.repository

import io.ruban.entity.Instrument
import io.ruban.util.active
import io.ruban.util.disabled
import io.ruban.util.updated
import org.slf4j.LoggerFactory

class Repository {

    private val log = LoggerFactory.getLogger(Repository::class.java)

    private val dataSource: HashMap<String, Instrument> = HashMap()

    fun allActive(): Collection<Instrument> {
        return dataSource.values.filter { it.isActive }
    }

    fun activate(isin: String, description: String) {
        if (dataSource.containsKey(isin)) {
            val entity = dataSource[isin]!!
            dataSource[isin] = entity.active()
        } else dataSource[isin] = Instrument(isin = isin, description = description)
    }

    fun disable(isin: String, description: String) {
        if (dataSource.containsKey(isin)) {
            val entity = dataSource[isin]!!
            dataSource[isin] = entity.disabled(description)
        } else log.warn("Instrument [$isin] not found")
    }

    fun update(isin: String, price: Double) {
        if (dataSource.containsKey(isin)) {
            val entity = dataSource[isin]!!
            if (entity.isActive) {
                dataSource[isin] = entity.updated(price)
            } else log.warn("Instrument [$isin] not active")
        } else log.warn("Instrument [$isin] not found")
    }
}
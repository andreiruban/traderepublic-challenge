package io.ruban.repository

import io.ruban.entity.Instrument

class InstrumentRepository {

    private val dataSource: HashMap<String, Instrument> = HashMap()

    fun findAll(): Collection<Instrument> {
        return dataSource.values
    }

    fun save(entity: Instrument) {
        dataSource[entity.isin] = entity
    }
}
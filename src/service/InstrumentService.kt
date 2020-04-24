package io.ruban.service

import io.ruban.entity.Instrument
import io.ruban.repository.InstrumentRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


class InstrumentService : KodeinAware {
    override val kodein = Kodein {
        bind<InstrumentRepository>(tag = "repository") with singleton { InstrumentRepository() }
    }

    val repository by kodein.instance<InstrumentRepository>("repository")

    fun getAll(): Collection<Instrument> {
        return repository.findAll()
    }

    fun save(instrument: Instrument) {
        repository.save(instrument)
    }
}
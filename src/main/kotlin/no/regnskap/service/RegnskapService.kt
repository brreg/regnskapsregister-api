package no.regnskap.service

import no.regnskap.generated.model.Regnskap
import no.regnskap.repository.RegnskapRepository
import org.springframework.stereotype.Service
import java.util.Optional

import no.regnskap.mapper.persistenceToGenerated

@Service
class RegnskapService (private val regnskapRepository: RegnskapRepository) {

    fun getById(id: String): Optional<Regnskap> =
        regnskapRepository
            .findById(id)
            .flatMap { persistedData -> Optional.of(persistenceToGenerated(persistedData)) }

    fun getByOrgnr(orgnr: String): List<Regnskap> =
        regnskapRepository
            .findByOrgnr(orgnr)
            .map { dto -> persistenceToGenerated(dto) }
}

package no.regnskap.service

import no.regnskap.generated.model.Regnskap
import no.regnskap.repository.RegnskapRepository
import org.springframework.stereotype.Service

import no.regnskap.mapper.mapPersistenceToGenerated
import java.util.*

@Service
class RegnskapService (private val regnskapRepository: RegnskapRepository) {

    fun getById(id: String): Optional<Regnskap> =
        regnskapRepository
            .findById(id)
            .flatMap { Optional.of(it.mapPersistenceToGenerated()) }

    fun getByOrgnr(orgnr: String): List<Regnskap> =
        regnskapRepository
            .findByOrgnrOrderByJournalnrDesc(orgnr) // Sort by journalnr, highest value is the most recent data
            .distinctBy { it.regnaar } // Filters the list by the first objects with a distinct year, since it's already sorted the list will be the most recent data for each year
            .maxBy { it.regnaar } // Only return data from the last registered year
            .let {
                if(it != null) Collections.singletonList(it.mapPersistenceToGenerated())
                else emptyList()
            } // Return as list
}

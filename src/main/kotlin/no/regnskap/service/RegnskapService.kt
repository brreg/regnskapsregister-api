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
            .map { it.mapPersistenceToGenerated() }

    fun getByOrgnr(orgnr: String): List<Regnskap> =
        regnskapRepository
            .findByOrgnrOrderByJournalnrDesc(orgnr) // Sort by journalnr, highest value is the most recent data
            .filter { it.aarsregnskapstype.toLowerCase() == Regnskap.OppstillingsplanEnum.STORE.value } // "Husk også at det er ikke alle regnskapene som ligger i masse-filene som skal være tilgjengelig, kun de som følger "store" oppstillingsplaner"
            .distinctBy { it.regnaar } // Filters the list by the first objects with a distinct year, since it's already sorted the list will be the most recent data for each year
            .maxBy { it.regnaar } // Only return data from the last registered year
            .let {
                if(it != null) Collections.singletonList(it)
                else emptyList()
            } // Return as list
            .map { it.mapPersistenceToGenerated() }
}

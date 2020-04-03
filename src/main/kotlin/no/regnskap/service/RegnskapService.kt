package no.regnskap.service

import no.regnskap.RegnskapUtil
import no.regnskap.generated.model.Regnskap
import no.regnskap.repository.RegnskapRepository
import org.springframework.stereotype.Service

import no.regnskap.mapper.mapPersistenceToGenerated
import no.regnskap.repository.RegnskapLogRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import java.util.*

private const val REGNSKAPSTYPE_SELSKAP = "s"

@Service
class RegnskapService (
    private val regnskapRepository: RegnskapRepository,
    private val logRepository: RegnskapLogRepository
) {

    fun getById(id: String, year: Int?): Regnskap? =
        regnskapRepository
            .findByIdOrNull(id)
            ?.mapPersistenceToGenerated()
            ?.let {regnskap ->
                if (year == null || RegnskapUtil.forYear(regnskap.regnskapsperiode, year)) regnskap
                else null
            }

    fun getByOrgnr(orgnr: String, year: Int?): List<Regnskap> =
        regnskapRepository
            .findByOrgnrOrderByJournalnrDesc(orgnr) // Sort by journalnr, highest value is the most recent data
            .filter { it.aarsregnskapstype.toLowerCase() == Regnskap.OppstillingsplanEnum.STORE.value } // "Husk også at det er ikke alle regnskapene som ligger i masse-filene som skal være tilgjengelig, kun de som følger "store" oppstillingsplaner"
            .filter { it.regnskapstype.toLowerCase() == REGNSKAPSTYPE_SELSKAP } // "Men for konsernselskap, så leverer den regnskapet for konsernet, og ikke for selskapet. Her må det endres slik at det er regnskapet for selskapet som leveres. Dette fremkommer av feltet "regnskapstype". Her har du verdiene S og K, der K står for konsern. Vi skal kun vise dem som har regnskapstype S, og ikke K."
            ?.filter {regnskap ->
                if (year == null) true
                else regnskap.regnaar == year
            }
            .distinctBy { it.regnaar } // Filters the list by the first objects with a distinct year, since it's already sorted the list will be the most recent data for each year
            .maxBy { it.regnaar } // Only return data from the last registered year
            .let {
                if(it != null) Collections.singletonList(it)
                else emptyList()
            } // Return as list
            .map { it.mapPersistenceToGenerated() }

    fun getLog(): List<String> =
        logRepository
            .findAll(Sort.by(Sort.Direction.ASC, "filename"))
            .map { entry -> entry.filename }
}

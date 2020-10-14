package no.regnskap.service;

import no.regnskap.generated.model.Regnskap;
import no.regnskap.generated.model.Regnskapstype;
import no.regnskap.mapper.RegnskapFieldsMapper;
import no.regnskap.repository.RegnskapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.regnskap.repository.RegnskapLogRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class RegnskapService {

    @Autowired
    private RegnskapRepository regnskapRepository;


    public Regnskap getById(final String id, final RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode) throws SQLException {
        return regnskapRepository.getById(id, regnskapFieldIncludeMode);
    }

    public List<Regnskap> getByOrgnr(final String orgnr, final Integer år, final Regnskapstype regnskapstype, final RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode) throws SQLException {
        return regnskapRepository.getByOrgnr(orgnr, år, regnskapstype, regnskapFieldIncludeMode);
    }

    public List<String> getLog() throws SQLException {
        return regnskapRepository.getLog();
    }

}
/*
class RegnskapService (
    private val regnskapRepository: RegnskapRepository,
    private val logRepository: RegnskapLogRepository
) {

    fun getById(id: String): Regnskap? =
        regnskapRepository
            .findByIdOrNull(id)
            ?.mapPersistenceToGenerated()

    fun getByOrgnr(orgnr: String, år: Int?, regnskapstypeKode: String?): List<Regnskap> =
        regnskapRepository
            .findByOrgnrOrderByJournalnrDesc(orgnr) // Sort by journalnr, highest value is the most recent data
            .filter { it.aarsregnskapstype.toLowerCase() == Regnskap.OppstillingsplanEnum.STORE.value } // "Husk også at det er ikke alle regnskapene som ligger i masse-filene som skal være tilgjengelig, kun de som følger "store" oppstillingsplaner"
            ?.filter {regnskap ->
                if (år == null) true
                else regnskap.regnaar == år
            }
            ?.filter {regnskap ->
                if (regnskapstypeKode == null) true
                else regnskap.regnskapstype.equals(regnskapstypeKode, true)
            }
            .distinctBy { it.regnskapstype to it.regnaar } // Only return one Selskap and one Konsern per year
            .let {
                if(it != null) it
                else emptyList()
            } // Return as list
            .map { it.mapPersistenceToGenerated() }

    fun getLog(): List<String> =
        logRepository
            .findAll(Sort.by(Sort.Direction.ASC, "filename"))
            .map { entry -> entry.filename }

    companion object {
        fun regnskapstypeToKode(regnskapstype: String?): String? =
                when (regnskapstype) {
                    "selskap" -> REGNSKAPSTYPE_SELSKAP
                    "konsern" -> REGNSKAPSTYPE_KONSERN
                    else -> null
                }
    }
}
*/

package no.brreg.regnskap.service;

import no.brreg.regnskap.generated.model.Regnskap;
import no.brreg.regnskap.generated.model.Regnskapstype;
import no.brreg.regnskap.mapper.RegnskapFieldsMapper;
import no.brreg.regnskap.repository.RegnskapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;


@Service
public class RegnskapService {

    @Autowired
    private RegnskapRepository regnskapRepository;


    public List<Regnskap> getByOrgnr(final String orgnr, final Integer id, final Integer år, final Regnskapstype regnskapstype, final RegnskapFieldsMapper.RegnskapFieldIncludeMode regnskapFieldIncludeMode) throws SQLException {
        return regnskapRepository.getByOrgnr(orgnr, id, år, regnskapstype, regnskapFieldIncludeMode);
    }

    public List<String> getLog() throws SQLException {
        return regnskapRepository.getLog();
    }

}

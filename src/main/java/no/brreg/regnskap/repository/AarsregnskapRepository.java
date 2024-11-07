package no.brreg.regnskap.repository;

import no.brreg.regnskap.model.AarsregnskapFileMeta;

import java.util.List;

public interface AarsregnskapRepository {
    List<AarsregnskapFileMeta> getAarsregnskapMeta(String orgnr);
}

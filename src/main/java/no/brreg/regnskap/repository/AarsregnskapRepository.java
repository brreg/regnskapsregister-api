package no.brreg.regnskap.repository;

import java.util.List;
import java.util.Optional;

public interface AarsregnskapRepository {
    List<String> getAvailableAarsregnskap(String orgnr);
    Optional<String> getAarsregnskapPath(String orgnr, String year);
}

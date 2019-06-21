package no.regnskap.repository;

import no.regnskap.model.RegnskapDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegnskapRepository extends MongoRepository<RegnskapDB, String> {
    List<RegnskapDB> findByOrgnrOrderByJournalnrDesc(String orgnr);
}
